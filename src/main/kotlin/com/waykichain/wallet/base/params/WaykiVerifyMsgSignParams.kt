package com.waykichain.wallet.base.params

import com.sun.org.apache.xpath.internal.operations.Bool
import org.bitcoinj.core.*


class WaykiVerifyMsgSignParams(val signature:String ,val publicKey:String ,val msg :String,val netParams: NetworkParameters){

    var address : String? = null
    var isValid : Boolean = false

    fun checkParams():Boolean{
        if ((publicKey.length != 66) || (signature.length % 2 != 0)){
            println("The length of publicKey or signature error")
            return false
        }
        return true
    }

    fun checkPublicKey(): Boolean {
        return this.checkParams() && ECKey.isPubKeyCanonical(Utils.HEX.decode(publicKey))
    }

   fun getAddressFromPublicKey(): String? {

        if (this.checkPublicKey()){
            val ecKey=ECKey.fromPublicOnly(Utils.HEX.decode(publicKey))
            address = LegacyAddress.fromKey(netParams,ecKey).toBase58()
        }

        return address
    }

    fun verifyMsgSignature(): VerifyMsgSignatureResult{

        val addrFromPubKey = this.getAddressFromPublicKey()
        var verifyMsgSignatureResult = VerifyMsgSignatureResult(false,"")

        if (addrFromPubKey != null){
            val msgBytes = msg.toByteArray()
            val signatureBytes = Utils.HEX.decode(signature)
            //Use sha256_sha160 instead of sha256_twice
            val data = Sha256Hash.hash(Utils.sha256hash160(msgBytes))
            isValid = ECKey.verify(data,signatureBytes,Utils.HEX.decode(publicKey))

            if (isValid) {
                verifyMsgSignatureResult = VerifyMsgSignatureResult(isValid, addrFromPubKey)
                return verifyMsgSignatureResult
            }
        }

        return verifyMsgSignatureResult
    }

    data class VerifyMsgSignatureResult(var isValid: Boolean, var address: String?)
}
