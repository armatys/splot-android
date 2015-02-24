package io.splot;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;


import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayDeque;

/** Splot engine.
 * Created by mako on 09.09.2014.
 */
public class SplotEngine {
    private static final String TAG = "Splot";

    private Context mContext;
    private LuaState mLuaState;
    private ArrayDeque<Integer> mStackIndexes;

    public SplotEngine(Context context) {
        mContext = context;
        mLuaState = newState(context);
        mStackIndexes = new ArrayDeque<Integer>();
    }

    /** Returns the main Lua state used by this engine instance. */
    public LuaState getLuaState() {
        return mLuaState;
    }

    /** Stores the current number of elements on the Lua stack. */
    public void saveStack() {
        final int n = mLuaState.getTop();
        mStackIndexes.addLast(n);
    }

    /** Restores the Lua stack to the number of elements previously saved by {@code saveStack}. */
    public void restoreStack() {
        final Integer n = mStackIndexes.pollLast();
        if (n != null) {
            mLuaState.setTop(n);
        } else {
            throw new RuntimeException("Unbalanced call to restore the Lua stack.");
        }
    }

    /** Loads a Lua module from the app's assets. */
    public Pair<Boolean, String> loadLuaModule(String luaModuleName) throws IOException {
        luaModuleName = luaModuleName.replaceAll("\\.", "/");
        final InputStream is = mContext.getAssets().open("splot_lua/" + luaModuleName + ".lua");
        return loadInputStream(luaModuleName, is);
    }

    /** Loads a Lua module using an input stream. */
    public Pair<Boolean, String> loadInputStream(String luaModuleName, InputStream luaFileInputStream) throws IOException {
        byte[] bytes = readAllBytes(luaFileInputStream);
        final int loadResult = mLuaState.LloadBuffer(bytes, luaModuleName);

        if (loadResult != 0) {
            final String errMsg = mLuaState.toString(-1);
            return new Pair<Boolean, String>(Boolean.FALSE, errMsg);
        }

        final int pcallResult = mLuaState.pcall(0, LuaState.LUA_MULTRET, 0);
        if (pcallResult != 0) {
            final String errMsg = mLuaState.toString(-1);
            return new Pair<Boolean, String>(Boolean.FALSE, errMsg);
        }

        return new Pair<Boolean, String>(Boolean.TRUE, "");
    }

    /** Creates a new reference for the table on top of the stack.
     * @return Unique reference number.
     */
    public int addTableReference() {
        return mLuaState.Lref(LuaState.LUA_REGISTRYINDEX);
    }
    
    /** Removes the given reference from the registry. */
    public void removeTableReference(int ref) {
        mLuaState.LunRef(LuaState.LUA_REGISTRYINDEX, ref);
    }

    /** Create a new Lua state. */
    private static LuaState newState(Context context) {
        LuaState luaState = LuaStateFactory.newLuaState();
        luaState.openLibs();

        luaState.getGlobal("package");
        luaState.getField(-1, "searchers");
        int nLoaders = luaState.rawlen(-1);

        // Move the default loaders up, since we want our loader to be the first on the list.
        for (int i = nLoaders; i >= 1; i--) {
            luaState.rawGetI(-1, i);
            luaState.rawSetI(-2, i + 1);
        }

        try {
            new LuaPrintFunction(luaState).register("print");
        } catch (LuaException e) {
            throw new RuntimeException("Could not register 'print' function: " + e.getLocalizedMessage());
        }

        try {
            luaState.pushJavaFunction(new AssetLoaderFunction(luaState, context));   // package searchers searcher
            luaState.rawSetI(-2, 1);       // package searchers
            luaState.setTop(0);
        } catch (LuaException e) {
            throw new RuntimeException("Could not set the asset loader function: " + e.getLocalizedMessage());
        }

        return luaState;
    }

    /** The implementation for Lua's {@code print} function.
     * The default implementation needs to be replaced, because we
     * want to write using the Android's {@link android.util.Log}. 
     */
    private static class LuaPrintFunction extends JavaFunction {
        private LuaPrintFunction(LuaState L) {
            super(L);
        }

        @Override public int execute() throws LuaException {
            final StringBuilder builder = new StringBuilder("");
            for (int i = 2, max = L.getTop(); i <= max; i++) {
                final String s;
                final int type = L.type(i);
                if (type == LuaState.LUA_TUSERDATA) {
                    Object obj = L.toJavaObject(i);
                    if (obj != null) {
                        s = obj.toString();
                    } else {
                        s = L.toString(i);
                    }
                } else if (type == LuaState.LUA_TBOOLEAN) {
                    s = L.toBoolean(i) ? "true" : "false";
                } else {
                    s = L.toString(i);
                }
                builder.append(s);
                if (i < max) {
                    builder.append("\t");
                }
            }
            Log.d(TAG, builder.toString());
            return 0;
        }
    }

    /** A Lua loader, that loads Lua modules from app's assets. */
    private static class AssetLoaderFunction extends JavaFunction {
        private Context mContext;

        private AssetLoaderFunction(LuaState L, Context context) {
            super(L);
            mContext = context;
        }

        @Override public int execute() throws LuaException {
            final Resources resources = mContext.getResources();
            final String originalName = L.toString(-1);
            final String assetName = originalName.replaceAll("\\.", "/");
            final String rawName = "splot_lua_" + originalName.replaceAll("\\.", "_");
            InputStream is;

            try {
                is = mContext.getAssets().open("splot_lua/" + assetName + "/init.lua");
            } catch (IOException e) {
                is = null;
            }

            if (is == null) {
                try {
                    is = mContext.getAssets().open("splot_lua/" + assetName + ".lua");
                } catch (IOException e) {
                    is = null;
                }
            }

            if (is == null) {
                try {
                    final int rawId = R.raw.class.getField(rawName).getInt(null);
                    is = resources.openRawResource(rawId);
                } catch (Exception e) {
                    is = null;
                }
            }

            if (is != null) {
                try {
                    byte[] bytes = readAllBytes(is);
                    L.LloadBuffer(bytes, rawName);
                    return 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            L.pushString("\nno file '" + rawName + "' in 'lua' directory");
            return 1;
        }
    }

    /** Reads all the data from the input stream. */
    private static ByteArrayOutputStream readAll(InputStream inputStream) throws IOException {
        final int N = 4096;
        final ByteArrayOutputStream output = new ByteArrayOutputStream(N);
        final byte[] buffer = new byte[N];
        while (true) {
            final int n = inputStream.read(buffer);
            if (n >= 0) {
                output.write(buffer, 0, n);
            } else {
                break;
            }
        }
        return output;
    }

    /** Reads all the data from the input stream. */
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        return readAll(inputStream).toByteArray();
    }
}
