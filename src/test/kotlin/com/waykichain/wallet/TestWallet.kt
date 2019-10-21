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

import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.WaykiRegId
import com.waykichain.wallet.base.params.WaykiMainNetParams
import com.waykichain.wallet.base.params.WaykiTestNetParams
import com.waykichain.wallet.base.types.encodeInOldWay
import com.waykichain.wallet.impl.LegacyWallet
import com.waykichain.wallet.util.ContractUtil
import org.bitcoinj.core.*
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.junit.Test
import org.slf4j.LoggerFactory
import org.waykichain.wallet.util.BIP44Util
import org.waykichain.wallet.util.MnemonicUtil
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * @Author: Richard Chen
 * @Date 2018/08/29 下午6:27
 */

class TestWallet {

    private val logger = LoggerFactory.getLogger(javaClass)

    /*
    * 生成助记词 generate Mnemonic
    * */
    @Test
    fun  generateMnemonic(){
        var words: List<String>
        while (true) {
            words = MnemonicUtil.randomMnemonicCodes()
            if (words[0] == "vote")
                break
        }

        logger.info(words.toString())
    }

    /*
    * 生成维基链钱包 generate WaykiChain Wallet
    * */
    @Test
    fun generateWalletFromMnemonic(){
        val words = "vote despair mind rescue crumble choice garden elite venture cattle oxygen voyage"
        val networkParameters = WaykiTestNetParams.instance //generate Testnet Address From Mnemonic
        //val networkParameters = WaykiMainNetParams.instance //generate Mainnet Address From Mnemonic
       val wallet= BIP44Util.generateWaykiWallet(words,networkParameters)
        logger.info("\nmnemonic: $words\naddress:   ${wallet.address} \n Private key: ${wallet.privateKey} \nPublic Key: ${wallet.pubKey}")
    }

    @Test
    fun genMainnetAddressFromMnemonic() {
        System.out.println("Please enter 12-word mnemonic phrase below: \n")
        val words = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: {$words}")

        val networkParameters = WaykiTestNetParams.instance //generate Testnet Address From Mnemonic
        //val networkParameters = WaykiMainNetParams.instance //generate Mainnet Address From Mnemonic
        val wallet= BIP44Util.generateWaykiWallet(words,networkParameters)
        logger.info("\nmnemonic: $words\naddress:   ${wallet.address} \n Private key: ${wallet.privateKey} \nPublic Key: ${wallet.pubKey}")
    }

    @Test
    fun generateHDWallet(){
        val words = MnemonicUtil.randomMnemonicCodes()
        val seed = DeterministicSeed(words, null, "", 0L)
        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
        val networkParameters = WaykiMainNetParams.instance
        val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
        val address = LegacyAddress.fromPubKeyHash(networkParameters, mainKey.pubKeyHash).toString()
        val ecKey = ECKey.fromPrivate(mainKey.privKey)
        logger.info("\nmnemonic: $words\naddress:   $address \n privatekey:   ${ecKey.getPrivateKeyAsWiF(networkParameters)}")

    }

    @Test
    fun testGenerateKeyMainNet() {
        val wallet = LegacyWallet()
        val walletAddress = wallet.generateWalletAddress(WaykiNetworkType.MAIN_NET)
        val privKey = walletAddress.privKey
        val address = walletAddress.address
        logger.info("\n\n$privKey \n$address\n\n")
        /**
         * PhKmEa3M6BJERHdStG7nApRwURDnN3W48rhrnnM1fVKbLs3jaYd6
         * WZ9gVk4sgBuW9oJVtsE2gos5aLXK7rEEwC
         */
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
        val params = WaykiTestNetParams.instance //测试链
        val privKeyWiF = "Y7UiVRpTAZNDtZakSHZwebHD6romu9jcuj1tjjujzwbSqdKLCEQZ"//"YBb6tdJvQyD8VwxJ4HUjDfpcpmFc359uGFQLbegaaKr6FJY863iw"//"YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
        val address = "wbCG5rXEbEHQaw1FD9pbK1iUsBobxrbiJM"

        val key = DumpedPrivateKey.fromBase58(params, privKeyWiF).key

        val privKeyWiF2 = key.getPrivateKeyAsWiF(WaykiTestNetParams.instance)
        val address2 = LegacyAddress.fromPubKeyHash(params, key.pubKeyHash).toString()
        logger.info("$privKeyWiF \n$privKeyWiF2\n$address\n$address2\n\n")

        val legacyAddress = LegacyAddress.fromBase58(WaykiTestNetParams.instance, address)
        logger.info(Utils.HEX.encode(legacyAddress.hash))
        logger.info(Utils.HEX.encode(key.pubKeyHash))
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
       // System.out.println(hexStr)
    }
}
