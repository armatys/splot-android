package pl.makenika.app.splot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import org.keplerproject.luajava.LuaState;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pl.makenika.splot.LuaTable;
import pl.makenika.splot.SplotEngine;
public  class Test implements LuaTable {
    private SplotEngine mEngine;
    private String mLuaModuleName;
    private int mLuaTableRef;
    public Test(Context context) {
        mEngine = new SplotEngine(context);
        try {
            mEngine.loadLuaModule("test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mLuaTableRef = mEngine.getLuaState().Lref(LuaState.LUA_GLOBALSINDEX);
        mLuaModuleName = "test";
    }
    public SplotEngine getEngine() {
        return mEngine;
    }
    public int getLuaTableRef() {
        return mLuaTableRef;
    }
    protected void finalize() throws Throwable {
        super.finalize();
        mEngine.getLuaState().LunRef(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
    }
    private byte[] descr;
    public byte[] getDescr() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "descr");
        final byte[] data = L.toByteArray(-1);
        mEngine.restoreStack();
        return data;
    }
    public void setDescr(byte[] newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("descr");
        L.pushString(newValue);
        L.setTable(-3);
        mEngine.restoreStack();
    }
    public void callme() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "callme");
        L.call(0, 0);
        mEngine.restoreStack();
        return ;
    }
    public Pair<Double, byte[]> calculate(Double param1) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "calculate");
        L.pushNumber(param1);
        L.call(1, 2);
        Double ret1 = L.toNumber(-2);
        byte[] ret2 = L.toByteArray(-1);
        mEngine.restoreStack();
        return new Pair(ret1, ret2);
    }
    private Double answer;
    public Double getAnswer() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "answer");
        final Double a = L.toNumber(-1);
        mEngine.restoreStack();
        return a;
    }
    public void setAnswer(Double newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("answer");
        L.pushNumber(newValue);
        L.setTable(-3);
        mEngine.restoreStack();
    }
    private byte[] stranswer;
    public byte[] getStranswer() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "stranswer");
        final byte[] data = L.toByteArray(-1);
        mEngine.restoreStack();
        return data;
    }
    public void setStranswer(byte[] newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("stranswer");
        L.pushString(newValue);
        L.setTable(-3);
        mEngine.restoreStack();
    }
    public static class PTcomplexFn1 implements LuaTable {
        private int mLuaTableRef;
        private SplotEngine mEngine;
        public PTcomplexFn1(SplotEngine engine, boolean useTopTable) {
            mEngine = engine;
            if (!useTopTable) { mEngine.getLuaState().createTable(0, 0); }
            mLuaTableRef = mEngine.getLuaState().Lref(LuaState.LUA_GLOBALSINDEX);
        }
        public SplotEngine getEngine() {
            return mEngine;
        }
        public int getLuaTableRef() {
            return mLuaTableRef;
        }
        protected void finalize() throws Throwable {
            super.finalize();
            mEngine.getLuaState().LunRef(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        }
        private Double val;
        public Double getVal() {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.getField(-1, "val");
            final Double a = L.toNumber(-1);
            mEngine.restoreStack();
            return a;
        }
        public void setVal(Double newValue) {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushString("val");
            L.pushNumber(newValue);
            L.setTable(-3);
            mEngine.restoreStack();
        }
    }
    public static class RTcomplexFn1 implements LuaTable, Map<Double, byte[]> {
        private int mLuaTableRef;
        private SplotEngine mEngine;
        public RTcomplexFn1(SplotEngine engine, boolean useTopTable) {
            mEngine = engine;
            if (!useTopTable) { mEngine.getLuaState().createTable(0, 0); }
            mLuaTableRef = mEngine.getLuaState().Lref(LuaState.LUA_GLOBALSINDEX);
        }
        public SplotEngine getEngine() {
            return mEngine;
        }
        public int getLuaTableRef() {
            return mLuaTableRef;
        }
        protected void finalize() throws Throwable {
            super.finalize();
            mEngine.getLuaState().LunRef(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        }
        @Override public @Nullable byte[] get(Object indexObj) {
            if (!(indexObj instanceof Double)) {
                return null;
            }
            final Double index = (Double)indexObj;
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(index);
            L.getTable(-2);
            if (L.type(-1) == LuaState.LUA_TNIL) {
                return null;
            }
            byte[] data = L.toByteArray(-1);
            mEngine.restoreStack();
            return data;
        }
        @Override public @Nullable byte[] put(Double index, @Nullable byte[] newValue) {
            final byte[] oldMapping = get(index);
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(index);
            if (newValue != null) {
                L.pushString(newValue);
            } else {
                L.pushNil();
            }
            L.setTable(-3);
            mEngine.restoreStack();
            return oldMapping;
        }
        @Override public void clear() {
            final LuaState L = mEngine.getLuaState();
            mEngine.saveStack();
            L.createTable(0, 0);
            L.rawSetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            mEngine.restoreStack();
        }
        @Override public boolean containsKey(Object key) {
            return get(key) != null;
        }
        @Override public boolean containsValue(Object value) {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            while (L.next(-2) != 0) {
                if (Arrays.equals((byte[])value, L.toByteArray(-1))) { return true; }
                L.pop(1);
            }
            mEngine.restoreStack();
            return false;
        }
        @Override public @NonNull Set<Entry<Double, byte[]>> entrySet() {
            final HashSet<Entry<Double, byte[]>> hashSet = new HashSet<Entry<Double, byte[]>>();
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            while (L.next(-2) != 0) {
                Double key;
                key = L.toNumber(-2);
                byte[] value;
                value = L.toByteArray(-1);
                hashSet.add(new AbstractMap.SimpleEntry<Double, byte[]>(key, value));
                L.pop(1);
            }
            mEngine.restoreStack();
            return hashSet;
        }
        @Override public boolean isEmpty() {
            final LuaState L = mEngine.getLuaState();
            mEngine.saveStack();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            final boolean empty = L.next(-2) == 0;
            mEngine.restoreStack();
            return empty;
        }
        @Override public @NonNull Set<Double> keySet() {
            final HashSet<Double> hashSet = new HashSet<Double>();
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            while (L.next(-2) != 0) {
                Double key;
                key = L.toNumber(-2);
                hashSet.add(key);
                L.pop(1);
            }
            mEngine.restoreStack();
            return hashSet;
        }
        @Override public void putAll(@NonNull Map<? extends Double, ? extends byte[]> map) {
            for (Double key : map.keySet()) {
                put(key, map.get(key));
            }
        }
        @Override public @Nullable byte[] remove(Object key) {
            if (!(key instanceof Double)) {
                return null;
            }
            final Double index = (Double)key;
            return put(index, null);
        }
        @Override public int size() {
            final LuaState L = mEngine.getLuaState();
            int n = 0;
            mEngine.saveStack();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            while (L.next(-2) != 0) {
                n += 1;
                L.pop(1);
            }
            mEngine.restoreStack();
            return n;
        }
        @Override public @NonNull Collection<byte[]> values() {
            final ArrayList<byte[]> valuesArray = new ArrayList<byte[]>();
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNil();
            while (L.next(-2) != 0) {
                byte[] value;
                value = L.toByteArray(-1);
                valuesArray.add(value);
                L.pop(1);
            }
            mEngine.restoreStack();
            return valuesArray;
        }
    }
    public RTcomplexFn1 complexFn(PTcomplexFn1 param1) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "complexFn");
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, param1.getLuaTableRef());
        L.call(1, 1);
        L.pushValue(-1);
        RTcomplexFn1 ret1 = new RTcomplexFn1(mEngine, true);
        mEngine.restoreStack();
        return ret1;
    }
    public @Nullable Boolean whoKnows() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "whoKnows");
        L.call(0, 1);
        Boolean ret1 = null;
        if (L.type(-1) != LuaState.LUA_TNIL) {
            ret1 = L.toBoolean(-1);
        }
        mEngine.restoreStack();
        return ret1;
    }
    private @Nullable byte[] maybeSth;
    public @Nullable byte[] getMaybeSth() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "maybeSth");
        if (L.type(-1) == LuaState.LUA_TNIL) {
            mEngine.restoreStack();
            return null;
        }
        final byte[] data = L.toByteArray(-1);
        mEngine.restoreStack();
        return data;
    }
    public void setMaybeSth(@Nullable byte[] newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("maybeSth");
        if (newValue != null) {
            L.pushString(newValue);
        } else {
            L.pushNil();
        }
        L.setTable(-3);
        mEngine.restoreStack();
    }
    private Double newStuff;
    public Double getNewStuff() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.getField(-1, "newStuff");
        final Double a = L.toNumber(-1);
        mEngine.restoreStack();
        return a;
    }
    public void setNewStuff(Double newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("newStuff");
        L.pushNumber(newValue);
        L.setTable(-3);
        mEngine.restoreStack();
    }
    public static class Tarr implements LuaTable {
        private int mLuaTableRef;
        private SplotEngine mEngine;
        public Tarr(SplotEngine engine, boolean useTopTable) {
            mEngine = engine;
            if (!useTopTable) { mEngine.getLuaState().createTable(0, 0); }
            mLuaTableRef = mEngine.getLuaState().Lref(LuaState.LUA_GLOBALSINDEX);
        }
        public SplotEngine getEngine() {
            return mEngine;
        }
        public int getLuaTableRef() {
            return mLuaTableRef;
        }
        protected void finalize() throws Throwable {
            super.finalize();
            mEngine.getLuaState().LunRef(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        }
        private byte[] _1;
        public byte[] get_1() {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(1);
            L.getTable(-2);
            final byte[] data = L.toByteArray(-1);
            mEngine.restoreStack();
            return data;
        }
        public void set_1(byte[] newValue) {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(1);
            L.pushString(newValue);
            L.setTable(-3);
            mEngine.restoreStack();
        }
        private byte[] _2;
        public byte[] get_2() {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(2);
            L.getTable(-2);
            final byte[] data = L.toByteArray(-1);
            mEngine.restoreStack();
            return data;
        }
        public void set_2(byte[] newValue) {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushNumber(2);
            L.pushString(newValue);
            L.setTable(-3);
            mEngine.restoreStack();
        }
    }
    public Tarr getArr() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("arr");
        L.getTable(-2);
        Tarr t = new Tarr(mEngine, true);
        mEngine.restoreStack();
        return t;
    }
    public void setArr(Tarr newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("arr");
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, newValue.getLuaTableRef());
        L.setTable(-3);
        mEngine.restoreStack();
    }
    public static class Tinterf implements LuaTable {
        private int mLuaTableRef;
        private SplotEngine mEngine;
        public Tinterf(SplotEngine engine, boolean useTopTable) {
            mEngine = engine;
            if (!useTopTable) { mEngine.getLuaState().createTable(0, 0); }
            mLuaTableRef = mEngine.getLuaState().Lref(LuaState.LUA_GLOBALSINDEX);
        }
        public SplotEngine getEngine() {
            return mEngine;
        }
        public int getLuaTableRef() {
            return mLuaTableRef;
        }
        protected void finalize() throws Throwable {
            super.finalize();
            mEngine.getLuaState().LunRef(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        }
        private byte[] foo;
        public byte[] getFoo() {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.getField(-1, "foo");
            final byte[] data = L.toByteArray(-1);
            mEngine.restoreStack();
            return data;
        }
        public void setFoo(byte[] newValue) {
            mEngine.saveStack();
            final LuaState L = mEngine.getLuaState();
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
            L.pushString("foo");
            L.pushString(newValue);
            L.setTable(-3);
            mEngine.restoreStack();
        }
    }
    public @Nullable Tinterf getInterf() {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("interf");
        L.getTable(-2);
        if (L.type(-1) == LuaState.LUA_TNIL) {
            mEngine.restoreStack();
            return null;
        }
        Tinterf t = new Tinterf(mEngine, true);
        mEngine.restoreStack();
        return t;
    }
    public void setInterf(@Nullable Tinterf newValue) {
        mEngine.saveStack();
        final LuaState L = mEngine.getLuaState();
        L.rawGetI(LuaState.LUA_GLOBALSINDEX, mLuaTableRef);
        L.pushString("interf");
        if (newValue == null) { L.pushNil(); }
        else {
            L.rawGetI(LuaState.LUA_GLOBALSINDEX, newValue.getLuaTableRef());
        }
        L.setTable(-3);
        mEngine.restoreStack();
    }
}

