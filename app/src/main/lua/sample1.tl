local mod1 = require 'mod1'
local splot = require 'splot'
local submod1 = require 'mod1.submod1'

local _M = {}

_M.splotVersion = splot._VERSION
_M.mod1Name = mod1.name
_M.submod1Name = submod1.name

_M.stringProperty = "Hello world!"
_M.intProperty = 58827
_M.doubleProperty = 1860.368
_M.booleanProperty = true

local array: {number: string} = {'a', 'b'}
_M.array = array

_M.hardArray = {'c', 'd'}

local maybeString1: string? = "here"
_M.maybeString1 = maybeString1

local maybeString2: string? = nil
_M.maybeString2 = maybeString2

local deepInnerTable = {
    name = "deep inner table"
}

local innerTable = {
    name = "inner table",
    deepValue = deepInnerTable
}

_M.tableProperty = {
    name = "table property",
    value = innerTable
}

local interface IFoo
    foo: string
end

local foo: IFoo? = {foo='abc'}
_M.foo = foo

function _M.printFunction()
    print "Hello from function"
end

function _M.sum(a: number, b: number)
    return a + b
end

function _M.pairFunction(s: string, b: boolean)
    return "Hey " .. s, not b
end

function _M.makeBigger(n: number): number?
    if n <= 0.5 then
        return nil
    else
        return n * 10
    end
end

function _M.tableFunction(t: {"val": number}): {number: string}
    return {[t.val] = 'ok'}
end

return _M
