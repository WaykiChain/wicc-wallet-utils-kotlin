/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 The Waykichain Core developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.params.WaykiCommonTxParams
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.WaykiRegId
import com.waykichain.wallet.base.params.WaykiRegisterAccountTxParams
import com.waykichain.wallet.base.params.WaykiTestNetParams
import com.waykichain.wallet.base.types.encodeInOldWay
import com.waykichain.wallet.impl.LegacyWallet
import org.bitcoinj.core.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import org.slf4j.LoggerFactory

/**
 * @Author: Richard Chen
 * @Date 2018/08/29 下午6:27
 */

class TestWallet {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun testGenerateKeyMainNet() {
        val wallet = LegacyWallet()
        val walletAddress = wallet.generateWalletAddress(WaykiNetworkType.MAIN_NET)
        val privKey = walletAddress.privKey
        val address = walletAddress.address
        logger.info("\n\n$privKey \n$address\n\n")
    }

    @Test
    fun testGenerateKeyTestNet() {
        val wallet = LegacyWallet()
        val walletAddress = wallet.generateWalletAddress(WaykiNetworkType.TEST_NET)
        val privKey = walletAddress.privKey
        val address = walletAddress.address
        logger.info("\n\n$privKey \n$address\n\n")
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
    fun testGenerateRegisterAccountTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
//        val privKeyWiF = "YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
//        val privKeyWiF = "YBqQKuQQMBeiTUTMoP5ySPzbWpNUDZpRCCCgvS2LnKbF5jzKwg4p"
        val privKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        System.out.println("            ${key.publicKeyAsHex}")

        val txParams = WaykiRegisterAccountTxParams(key.pubKey, null, 30827+100, 10000)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        System.out.println(tx)

    }

    @Test
    fun testGenerateCommonTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destPrivKeyWif = "YB1ims24GnRCdrB8TJsiDrxos4S5bNS58qetjyFWhSDyxT9phCEa"
        val destKey = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWif).key
        val destAddress = LegacyAddress.fromPubKeyHash(netParams, destKey.pubKeyHash).toString()
        System.out.println("Send 1WICC From: $srcAddress To: $destAddress")

        val txParams = WaykiCommonTxParams(40498, 10000, 100000000, "30947-1", destKey.pubKeyHash)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        System.out.println(tx)
    }


    @Test
    fun testSnippet() {
        val ss = ByteArrayOutputStream()
//        ss.write(VarInt(10000).encode())
//        ss.write(VarInt(10000).encodeInOldWay())

        //regData: regHeight, regIndex
        val regId = WaykiRegId(30947,1)
        ss.write(VarInt(4).encodeInOldWay())
        ss.write(VarInt(regId.regHeight).encodeInOldWay())
        ss.write(VarInt(regId.regIndex).encodeInOldWay())

//        ss.write(10000.toBigInteger().toByteArray())
        val bytes = ss.toByteArray()
        val hexStr =  Utils.HEX.encode(bytes)
        System.out.println(hexStr)
    }
}
