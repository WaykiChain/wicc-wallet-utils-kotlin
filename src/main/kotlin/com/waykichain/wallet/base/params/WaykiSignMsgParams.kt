package com.waykichain.wallet.base.params

import org.bitcoinj.core.*


class WaykiSignMsgParams( val msg: String){

    var signature: ByteArray? = null
    var publicKey: String = ""

    fun getSignatureHash(): ByteArray {

        val msgBytes = msg.toByteArray()
        //Use sha256_sha160 instead of sha256_twice
        val sha256_sha160 = Sha256Hash.hash(Utils.sha256hash160(msgBytes))

        return sha256_sha160
    }

    fun signatureMsg(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        val ecSig = key.sign(Sha256Hash.wrap(sigHash))
        publicKey=key.publicKeyAsHex
        signature = ecSig.encodeToDER()

        return signature!!
    }

    fun serializeSignature(): SignResult {
        assert (signature != null)
        val signatureStr =  Utils.HEX.encode(signature)
        val signResult=SignResult(signatureStr,publicKey)

        return signResult
    }

    data class SignResult(var signature: String, var publicKey: String)

}
