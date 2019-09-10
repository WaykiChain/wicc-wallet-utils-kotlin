mylib = require "mylib"
--脚本中必须以`mylib = require "mylib"`开头，注意一定要放在第一行，第一行如果留空会报异常。

--定义合约调用事件
METHOD = {
    CHECK_HELLOWORLD  = 0x17,
    SEND_HELLOWORLD = 0x18
}

--参考[4.3 API调试方法实例]
--用于输出log信息至文件
LogMsg = function (msg)
   local logTable = {
        key = 0,
        length = string.len(msg),
        value = msg
  }
  mylib.LogPrint(logTable)
end

---------------------------------------------------

Check = function()
    LogMsg("Run CHECK_HELLOWORLD Method")
end

Send = function()
    LogMsg("Run SEND_HELLOWORLD Method")
end

--参考[4.2开发常用方法]
--智能合约入口
Main = function()
  assert(#contract >=2, "Param length error (<2): " ..#contract )
  assert(contract[1] == 0xf0, "Param MagicNo error (~=0xf0): " .. contract[1])

  if contract[2] == METHOD.CHECK_HELLOWORLD then
    Check()
  elseif contract[2] == METHOD.SEND_HELLOWORLD then
    Send()
  else
    error('method# '..string.format("%02x", contract[2])..' not found')
  end
end

Main()
