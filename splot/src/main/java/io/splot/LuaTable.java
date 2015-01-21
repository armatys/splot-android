package io.splot;

/** Interface for Lua tables.
 * Created by mako on 30.09.2014.
 */
public interface LuaTable {
    SplotEngine getEngine();
    int getLuaTableRef();
}
