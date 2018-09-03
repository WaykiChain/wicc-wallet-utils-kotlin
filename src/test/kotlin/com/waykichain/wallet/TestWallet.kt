package com.waykichain

import com.waykichain.wallet.WaykiNetworkType
import com.waykichain.wallet.WaykiRegisterAccountTxParams
import com.waykichain.wallet.WaykiTestNetParams
import com.waykichain.wallet.impl.LegacyWallet
import org.bitcoinj.core.*
import org.junit.Test

/**
 * @Author: Richard Chen
 * @Date 2018/08/29 下午6:27
 */

class TestWallet {

    @Test
    fun testGenerateKey() {

        val wallet = LegacyWallet()
        val walletAddress = wallet.generateWalletAddress(WaykiNetworkType.TEST_NET)
        val privKey = walletAddress.privKey
        val address = walletAddress.address

////        val params =
//        val params = WaykiTestNetParams.instance
//        val key = ECKey()
//        val pubKey = key.publicKeyAsHex
//        val privKey = key.privKey
//        val privKeyWiF = key.getPrivateKeyAsWiF(params)
//        val pubKeyHash = key.pubKeyHash
//        val address = LegacyAddress.fromPubKeyHash(params, pubKeyHash)
//
//        val key2 =  ECKey.fromPrivate(privKey, true)
//
//        val pubKey2 = key2.publicKeyAsHex

        System.out.print("$privKey \n$address\n\n")
    }

    @Test
    fun testImportPrivKey() {
        val params = WaykiTestNetParams.instance
        val privKeyWiF = "YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
        val address = "wbCG5rXEbEHQaw1FD9pbK1iUsBobxrbiJM"

        val key2 = DumpedPrivateKey.fromBase58(params, privKeyWiF).key
        val privKey2 = key2.getPrivateKeyAsWiF(WaykiTestNetParams.instance)
        val pubKeyHash = key2.pubKeyHash
        val address2 = LegacyAddress.fromPubKeyHash(params, pubKeyHash)
        System.out.print("$privKeyWiF \n$privKey2\n$address\n$address2\n\n")

    }

    @Test
    fun testGenerateRegAccountTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val privKeyWiF = "YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
        val address = "wbCG5rXEbEHQaw1FD9pbK1iUsBobxrbiJM"
        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        val txParams = WaykiRegisterAccountTxParams()
        txParams.signTx(key)
        val tx = wallet.createRegisterTransaction(txParams, key)
        System.out.println(tx)
    }
}