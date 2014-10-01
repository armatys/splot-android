local splot = require 'splot'
-- local testmod = require 'submod.testmod'
-- local test = require 'mod.test'

local _M = {}

_M.descr = "test module"
function _M.callme()
  print 'Being called'
  print('Version', splot._VERSION)
  -- print('Testmod', testmod.test)
  -- print('test', test.saySomething("Yo"))
end
local function calculate(a: number): (number, string)
local n: number = a * 2
return n, 'hi'
end
_M.calculate = calculate
_M.answer = 42
_M.stranswer = "fourty-two"

function _M.complexFn(t: {"val": number}): {number: string}
return {[t.val] = 'ok'}
end

function _M.whoKnows(): nil|boolean
return nil
end

local maybeSth: string? = nil
_M.maybeSth = maybeSth

_M.newStuff = 892

local arr: {number: string} = {'a', 'b'}
_M.arr = {'a', 'b'} --arr

local interface I
foo: string
end
local interf: I? = {foo='abc'}
_M.interf = interf

return _M
-- return {
--   name = "test mod",
--   callme = function()
--     print 'Hi'
--   end,
--   calculate = function(a: number): (number, string, nil*)
--     local n: number = a * 2
--     return n, 'hi'
--   end
-- }
