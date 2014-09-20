package pl.makenika.splot;

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

/** Splot engine.
 * Created by mako on 09.09.2014.
 */
public class SplotEngine {
    private static final String TAG = "Splot";

    private Context mContext;
    private LuaState mLuaState;

    public SplotEngine(Context context) {
        mContext = context;
        mLuaState = newState(context);
    }

    public Pair<Boolean, String> loadLuaModule(String luaModuleName) throws IOException {
        luaModuleName = luaModuleName.replaceAll("\\.", "/");
        final InputStream is = mContext.getAssets().open("splot_lua/" + luaModuleName + ".lua");
        return loadInputStream(is);
    }

    public Pair<Boolean, String> loadInputStream(InputStream luaFileInputStream) throws IOException {
        final String code = readWholeString(luaFileInputStream);
        final int ret = mLuaState.LdoString(code);
        if (ret == 1) {
            final String err = mLuaState.toString(-1);
            return new Pair<Boolean, String>(Boolean.FALSE, err);
        }
        return new Pair<Boolean, String>(Boolean.TRUE, "");
    }

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
            Log.d("Splot", builder.toString());
            return 0;
        }
    }

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
                is = mContext.getAssets().open("splot_lua/" + assetName + ".lua");
            } catch (IOException e) {
                is = null;
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

    private static LuaState newState(Context context) {
        LuaState L = LuaStateFactory.newLuaState();
        L.openLibs();

        L.getGlobal("package");
        L.getField(-1, "loaders");
        int nLoaders = L.objLen(-1);

        // Move the default loaders up, since we want our loader to be the first on the list.
        for (int i = nLoaders; i >= 1; i--) {
            L.rawGetI(-1, i);
            L.rawSetI(-2, i + 1);
        }

        try {
            new LuaPrintFunction(L).register("print");
        } catch (LuaException e) {
            throw new RuntimeException("Could not register 'print' function: " + e.getLocalizedMessage());
        }

        try {
            L.pushJavaFunction(new AssetLoaderFunction(L, context));   // package loaders loader
            L.rawSetI(-2, 1);       // package loaders
            L.setTop(0);
        } catch (LuaException e) {
            throw new RuntimeException("Could not set the asset loader function: " + e.getLocalizedMessage());
        }

        return L;
    }

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

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        return readAll(inputStream).toByteArray();
    }

    private static String readWholeString(InputStream inputStream) throws IOException {
        return readAll(inputStream).toString("UTF-8");
    }
}
