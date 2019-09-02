package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.cdpHash
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt

/**
 * srcRegId: (regHeight-regIndex PubKeyHash)
 * destAddr: 20-byte PubKeyHash
 * fee Minimum 0.001 wicc
 */
class WaykiCdpStakeTxParams(nValidHeight: Long, fees: Long = 1000L,
                            val userId: String, userPubKey: String, val cdpTxid: String? = cdpHash,
                            feeSymbol: String, val bCoinSymbol: String, val sCoinSymbol: String,
                            val bCoinToStake: Long, val sCoinToMint: Long) :
        BaseSignTxParams(feeSymbol, userPubKey, null, nValidHeight, fees, WaykiTxType.TX_CDPSTAKE, 1) {

    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        val cdpTxHex = Utils.HEX.decode(cdpTxid).reversedArray()
        val pubKey = Utils.HEX.decode(userPubKey)
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .add(cdpTxHex)
                .add(bCoinSymbol)
                .add(sCoinSymbol)
                .add(VarInt(bCoinToStake).encodeInOldWay())
                .add(VarInt(sCoinToMint).encodeInOldWay())
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
        val sigSize = signature!!.size
        val cdpTxHex = Utils.HEX.decode(cdpTxid).reversedArray()
        val pubKey = Utils.HEX.decode(userPubKey)
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .add(cdpTxHex)
                .add(bCoinSymbol)
                .add(sCoinSymbol)
                .add(VarInt(bCoinToStake).encodeInOldWay())
                .add(VarInt(sCoinToMint).encodeInOldWay())
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }
}
