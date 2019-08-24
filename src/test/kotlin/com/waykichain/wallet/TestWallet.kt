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
import com.waykichain.wallet.base.types.encodeInOldWay
import com.waykichain.wallet.impl.LegacyWallet
import org.bitcoinj.core.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import org.slf4j.LoggerFactory

import com.waykichain.wallet.base.OperVoteFund
import com.waykichain.wallet.base.VoteOperType
import com.waykichain.wallet.base.params.*
import com.waykichain.wallet.util.ContractUtil
import org.bitcoinj.params.MainNetParams

import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.waykichain.wallet.util.BIP44Util
import org.waykichain.wallet.util.MnemonicUtil

import java.math.BigInteger
import java.util.*


/**
 * @Author: Richard Chen
 * @Date 2018/08/29 下午6:27
 */

class TestWallet {

    private val logger = LoggerFactory.getLogger(javaClass)

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

    @Test
    fun importMnemonic(){
        //val wordList = "lounge enable orphan hire mule hunt physical gym else soft ladder crystal"
        val wordList = "vote despair mind rescue crumble choice garden elite venture cattle oxygen voyage"
        val words = wordList.split(" ")
        MnemonicUtil.validateMnemonics(words)
        val seed = DeterministicSeed(words, null, "", 0L)
        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
        val networkParameters = WaykiTestNetParams.instance
        val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
        val address = LegacyAddress.fromPubKeyHash(networkParameters, mainKey.pubKeyHash).toString()
        val ecKey = ECKey.fromPrivate(mainKey.privKey)
        logger.info("\nmnemonic: $wordList\naddress:   $address \n Private key: ${ecKey.getPrivateKeyAsWiF(networkParameters)} \nPublic Key: ${ecKey.publicKeyAsHex}")
    }

    @Test
    fun genMainnetAddressFromMnemonic() {
        System.out.println("Please enter 12-word mnemonic phrase below: \n")
        val words = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: {$words}")

        val wordList = words.split(" ")
        MnemonicUtil.validateMnemonics(wordList)

        val seed = DeterministicSeed(wordList, null, "", 0L)
        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
        val networkParameters = WaykiMainNetParams.instance
        val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
        val address = LegacyAddress.fromPubKeyHash(networkParameters, mainKey.pubKeyHash).toString()
        val ecKey = ECKey.fromPrivate(mainKey.privKey)
        logger.info("\nmnemonic: $words\naddress:   $address \nPrivate key:   ${ecKey.getPrivateKeyAsWiF(networkParameters)} \nPublic Key: ${ecKey.publicKeyAsHex}")
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
        val params = WaykiTestNetParams.instance
        val privKeyWiF = "YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
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
    fun testGenerateRegisterAccountTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val privKeyWiF ="Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"

        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        logger.info("            ${key.publicKeyAsHex}")

        val txParams = WaykiRegisterAccountTxParams(key.pubKey, null, 429821, 10000)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")

    }

    @Test
    fun testGenerateRegisterAccountTxForMainNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance
        System.out.println("Please enter WiF private key: ")
        val privKeyWiF = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: {$privKeyWiF}")
        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key

        System.out.println("        ${key.publicKeyAsHex}")
        val txParams = WaykiRegisterAccountTxParams(key.pubKey, null, 2235511, 10000)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")

    }

    @Test
    fun testGenerateRegisterAccountTxForMainNetGod() {
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance
        System.out.println("Please enter the private WiF key: ")
        val privKeyWiF = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: $privKeyWiF")
        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        logger.info("            ${key.publicKeyAsHex}")

        val txParams = WaykiRegisterAccountTxParams(key.pubKey, null, 1461025+100, 10000)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")

    }

    @Test
    fun testGenerateCommonTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destPrivKeyWif = "YB1ims24GnRCdrB8TJsiDrxos4S5bNS58qetjyFWhSDyxT9phCEa"
        val destKey = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWif).key
        val destAddr = LegacyAddress.fromPubKeyHash(netParams, destKey.pubKeyHash).toString()
        logger.info("Send 1 wicc from: $srcAddress to: $destAddr")

        val txParams = WaykiCommonTxParams(WaykiNetworkType.TEST_NET, 429637, 100660, 100000000,"423318-1",destAddr )//"30947-1", destAddr)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
    }

    @Test
    fun testGenerateCommonTxForMainNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance

        val srcPrivKeyWiF = "PhKmEa3M6BJERHdStG7nApRwURDnN3W48rhrnnM1fVKbLs3jaYd6"
//        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key //ECKey

        val privateKey = Utils.HEX.decode("11d0d715a2f813caed9236dcc67d0cef38b9dbc4afa7d9b5dfefeec5747870d5")
        val srcECKey = ECKey.fromPrivate(privateKey)
        val srcPrivKey = srcECKey.getPrivateKeyAsWiF(WaykiMainNetParams.instance)
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKey).key
        val srcPubKey = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()
        logger.info ("srcPubKey: $srcPubKey \n srcPrivKey: $srcPrivKey")


        val destAddr = "WQRwCMmQGy2XvpATTai6AtGhrRrdXDQzQh"
        val txParams = WaykiCommonTxParams(WaykiNetworkType.MAIN_NET, 1926165, 10000, 10000, "926152-1", destAddr)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
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

    @Test
    fun testGenerateDelegateTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6CFeJthSWMPRRcEu734u4ovBfjRp3ytngt9iGEfsMvqxPmKo2Vy"//"Y9HSQ4GGLUMhUYALoLQTZY42hUGW7dRBsmWy2TWWESZfqDAqKdCs"//"Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destPrivKeyWif1 = "YB1ims24GnRCdrB8TJsiDrxos4S5bNS58qetjyFWhSDyxT9phCEa"
        val destKey1 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWif1).key
        val destAddr1 = LegacyAddress.fromPubKeyHash(netParams, destKey1.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr1")

        val destPrivKeyWiF2 = "Y9HSQ4GGLUMhUYALoLQTZY42hUGW7dRBsmWy2TWWESZfqDAqKdCs"//"Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val destKey2 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWiF2).key
        val destAddr2 = LegacyAddress.fromPubKeyHash(netParams, destKey2.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr2")

        val array4 = OperVoteFund(VoteOperType.ADD_FUND.value,destKey1.pubKey,200000000)
        val array5 = OperVoteFund(VoteOperType.ADD_FUND.value,destKey2.pubKey,200000000)
        val array6 = arrayOf(array4,array5)
        val txParams = WaykiDelegateTxParams("25813-1", array6, 10000000,479796)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)
        logger.info(tx)
    }


    @Test
    fun testGenerateRevokeDelegateTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destPrivKeyWif1 = "YB1ims24GnRCdrB8TJsiDrxos4S5bNS58qetjyFWhSDyxT9phCEa"
        val destKey1 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWif1).key
        val destAddr1 = LegacyAddress.fromPubKeyHash(netParams, destKey1.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr1")

        val destPrivKeyWiF2 = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val destKey2 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWiF2).key
        val destAddr2 = LegacyAddress.fromPubKeyHash(netParams, destKey2.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr2")

        val array4 = OperVoteFund(VoteOperType.MINUS_FUND.value,destKey1.pubKey,200000000)
        val array5 = OperVoteFund(VoteOperType.MINUS_FUND.value,destKey2.pubKey,200000000)
        val array6 = arrayOf(array4,array5)
        val txParams = WaykiDelegateTxParams("25813-1", array6, 10000000,479874)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)
        logger.info(tx)
    }

    @Test
    fun testGenerateContractTx() {
        //以锁仓为例 锁仓90天
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance

        val srcPrivKeyWiF ="PhKmEa3M6BJERHdStG7nApRwURDnN3W48rhrnnM1fVKbLs3jaYd6"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        logger.info(LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString())

        val value=100000000L //锁仓一个WICC
        val header="f202" //需要调用的方法
        val appid="450687-1"//合约锁仓90天合约的ID
        val contract = header + ContractUtil.to2HexString4byte(value) + "00000000"
        logger.info(contract)
        val contractByte=ContractUtil.hexString2binaryString(contract)
        val txParams = WaykiContractTxParams(srcKey.pubKey,494454, 100000, value, "926152-1",appid,contractByte)
        txParams.signTx(srcKey)
        val tx = wallet.createContractTransactionRaw(txParams)
        logger.info(tx)
    }

    @Test
    fun signMessageByPrivateKey() {  //私钥签名公钥验证
        val msg = "Waykichain"
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        logger.info(srcKey.publicKeyAsHex)
        val hash = Sha256Hash.hashTwice(msg.toByteArray())
        val ecSig = srcKey.sign(Sha256Hash.wrap(Utils.HEX.encode(hash)))//私钥签名
        val signature =  ecSig.encodeToDER()
        val ecKey = ECKey.fromPublicOnly(Utils.HEX.decode(srcKey.publicKeyAsHex))
        try {
            val vertify=  ecKey.verify(hash, signature);//公钥验证
            logger.info("验证成功"+vertify)
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }
}
