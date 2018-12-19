package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.OperVoteFund
import com.waykichain.wallet.base.WaykiRegId
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt

class WaykiDelegateTxParams(val srcRegId: String, var voteLists: Array<OperVoteFund>, fees: Long, nValidHeight: Long) :
        BaseSignTxParams(null, null, nValidHeight, fees, WaykiTxType.TX_DELEGATE, 1) {
    override fun getSignatureHash(): ByteArray {
        val regId = parseRegId(srcRegId)!!
        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(VarInt(4).encodeInOldWay())
                .add(VarInt(regId.regHeight).encodeInOldWay())
                .add(VarInt(regId.regIndex).encodeInOldWay())
                .add(voteLists)
                .add(VarInt(fees).encodeInOldWay())

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
        val hashStr = Utils.HEX.encode(hash)
        System.out.println("hash: $hashStr")

        return hash
    }

    override fun signTx(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        val ecSig = key.sign(Sha256Hash.wrap(sigHash))
        signature = ecSig.encodeToDER()
        return signature!!
    }

    override fun serializeTx(): String {
        assert(signature != null)

        val regId = parseRegId(srcRegId)!!
        val sigSize = signature!!.size

        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(VarInt(4).encodeInOldWay())
                .add(VarInt(regId.regHeight).encodeInOldWay())
                .add(VarInt(regId.regIndex).encodeInOldWay())
                .add(voteLists)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }

    fun parseRegId(regId: String): WaykiRegId? {
        val arr = regId.split("-")
        if (!intOrString(arr[0])) return null
        if (!intOrString(arr[1])) return null
        val height = arr[0].toLong()
        val index = arr[1].toLong()
        return WaykiRegId(height, index)
    }

    fun intOrString(str: String): Boolean {
        val v = str.toIntOrNull()
        return when (v) {
            null -> false
            else -> true
        }
    }
}
