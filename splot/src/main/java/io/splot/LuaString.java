package io.splot;

/** Represents a Lua string.
 * Created by mako on 23.02.2015.
 */
public class LuaString {
    private byte[] mData;

    public LuaString(byte[] data) {
        mData = data;
    }
    
    public LuaString(String s) {
        mData = s.getBytes();
    }

    public byte[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        return new String(mData);
    }
}
