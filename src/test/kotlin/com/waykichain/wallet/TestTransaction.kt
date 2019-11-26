package com.waykichain.wallet

import com.waykichain.wallet.base.*
import com.waykichain.wallet.base.params.*
import com.waykichain.wallet.impl.LegacyWallet
import com.waykichain.wallet.util.ContractUtil
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress
import org.bitcoinj.core.Utils
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File


class TestTransaction {
    private val logger = LoggerFactory.getLogger(javaClass)
    /*
    * 账户注册交易,新版本已基本废弃，可改用公钥注册，免注册费用
    * Account registration transaction, the new version has been abandoned, you can use public key registration, free registration fee
     * fee Minimum 0.1 wicc
    * */
    @Test
    fun testGenerateRegisterAccountTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val privKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"

        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        logger.info("            ${key.publicKeyAsHex}")

        val txParams = WaykiRegisterAccountTxParams(key.publicKeyAsHex, null, 429821, 10000000, CoinType.WICC.type)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")

    }

    /*
    * 测试网转账交易
    * Test network transfer
    * fee Minimum 0.1 wicc
    * */
    @Test
    fun testGenerateCommonTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val pubKey = srcKey.publicKeyAsHex
        val destAddr = "wWTStcDL4gma6kPziyHhFGAP6xUzKpA5if"
        val memo="test transfer"
        val txParams = WaykiCommonTxParams(WaykiNetworkType.TEST_NET, 166690, pubKey,100000000,
                100000000, "32714-5", destAddr,memo)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
    }

    /*
    * 主网转账交易
    * Main network transfer
    * fee Minimum 0.1 wicc
    * */
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
        logger.info("srcPubKey: $srcPubKey \n srcPrivKey: $srcPrivKey")
        val pubKey = srcKey.publicKeyAsHex
        val memo="测试转账"
        val destAddr = "WQRwCMmQGy2XvpATTai6AtGhrRrdXDQzQh"
        val txParams = WaykiCommonTxParams(WaykiNetworkType.MAIN_NET, 1926165, pubKey,10000, 10000,
                "926152-1", destAddr, memo)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
    }


    /*
   * 多币种转账交易 ,支持多种币种转账
   * Test nUniversal Coin Transfer Tx
   * fee Minimum 0.01 wicc
   * */
    @Test
    fun testGenerateUCoinTransferTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val pubKey = srcKey.publicKeyAsHex  //user publickey hex string
        val nValidHeight = 1184008L
        val coinSymbol = CoinType.WICC.type  //coind symbol
        val coinAmount = 10000L    //transfer amount
        val feeSymbol = CoinType.WICC.type
        val fees = 10000000L
        val regid = ""
        val destAddr = "wLKf2NqwtHk3BfzK5wMDfbKYN1SC3weyR4"
        val memo = "转账"

        val dest1=UCoinDest(LegacyAddress.fromBase58(netParams,destAddr),coinSymbol,coinAmount)
        val dests= arrayListOf<UCoinDest>(dest1)

        val txParams = WaykiUCoinTxParams(nValidHeight, regid, pubKey, dests.toList() , feeSymbol, fees, memo)
        txParams.signTx(srcKey)
        val tx = wallet.createUCoinTransactionRaw(txParams)
        logger.info("$tx")
    }

    /*
    * 投票交易
    * Voting transaction
    * fee Minimum 0.01 wicc
    * */
    @Test
    fun testGenerateDelegateTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6CFeJthSWMPRRcEu734u4ovBfjRp3ytngt9iGEfsMvqxPmKo2Vy"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destPrivKeyWif1 = "YB1ims24GnRCdrB8TJsiDrxos4S5bNS58qetjyFWhSDyxT9phCEa"
        val destKey1 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWif1).key //Public key of the person being voted
        val destAddr1 = LegacyAddress.fromPubKeyHash(netParams, destKey1.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr1")

        val destPrivKeyWiF2 = "Y9HSQ4GGLUMhUYALoLQTZY42hUGW7dRBsmWy2TWWESZfqDAqKdCs"
        val destKey2 = DumpedPrivateKey.fromBase58(netParams, destPrivKeyWiF2).key//Public key of the person being voted
        val destAddr2 = LegacyAddress.fromPubKeyHash(netParams, destKey2.pubKeyHash).toString()
        logger.info("Vote 1 wicc from: $srcAddress to: $destAddr2")

        //VoteOperType.ADD_FUND  投票
        //VoteOperType.MINUS_FUND //撤销投票
        val array4 = OperVoteFund(VoteOperType.ADD_FUND.value, destKey1.pubKey, 200000000)
        val array5 = OperVoteFund(VoteOperType.ADD_FUND.value, destKey2.pubKey, 200000000)
        val array6 = arrayOf(array4, array5)
        val txParams = WaykiDelegateTxParams("25813-1",srcKey.publicKeyAsHex, array6, 10000000, 479796)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
    * 多币种合约调用交易
    * Contract transaction sample
    * fee Minimum 0.01 wicc
    * */
    @Test
    fun testGenerateUCoinContractTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        logger.info(LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString())

        val value = 100000000L
        val appid = "450687-1"
        val contractByte = ContractUtil.hexString2binaryString("f001")
        val txParams = WaykiUCoinContractTxParams(srcKey.publicKeyAsHex, 727702,
                1000000, value, "0-1",
                appid, contractByte, CoinType.WICC.type,CoinType.WUSD.type)
        txParams.signTx(srcKey)
        val tx = wallet.createUCoinContractInvokeRaw(txParams)
        logger.info(tx)
    }

    /*
    * 合约调用交易
    * Contract transaction sample
    * fee Minimum 0.01 wicc
    * */
    @Test
    fun testGenerateContractTx() {
        //WRC20 Transfer
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val regId = "926152-1"
        val appId = "128711-1"
        val wrc20Amount = 100000000L // transfer 10000 WRC
        val destAddress="wNPWqv9bvFCnMm1ddiQdH7fUwUk2Qgrs2N"
        val contractByte = ContractUtil.transferWRC20Contract(wrc20Amount,destAddress)
        val txParams = WaykiContractTxParams(srcKey.publicKeyAsHex, 494454, 100000, 0, regId, appId, contractByte, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createContractTransactionRaw(txParams)
        logger.info(tx)
    }


    /*
    * 创建,追加cdp交易
    * Create or append an  cdp transaction
    * fee Minimum 0.01 wicc
    * */
    @Test
    fun testGenerateCdpStakeTx() {
        val nValidHeight = 5003L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WUSD.type  //fee symbol
        val bCoinSymbol = CoinType.WICC.type //stake coin symbol
        val sCoinSymbol = CoinType.WUSD.type  // get coind symbol
        val bCoinToStake = 100000000L  //stake amount
        val sCoinToMint = 50000000L   //get amount

        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val map= mapOf<String,Long>(Pair(bCoinSymbol,bCoinToStake));
        val txParams = WaykiCdpStakeTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, map ,sCoinSymbol,sCoinToMint)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpStakeTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
   * 赎回cdp交易
   * Redeem cdp transaction
   * fee Minimum 0.01 wicc
   * */
    @Test
    fun testRedeemCdpTx() {
        val nValidHeight = 8510L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val sCoinsToRepay = 50000000L  //repay amount
        val redeemSymbol = CoinType.WICC.type  //redeem asset symbol
        val bCoinsToRedeem = 100000000L   //redeem amount

        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val map= mapOf<String,Long>(Pair(redeemSymbol,bCoinsToRedeem));
        val txParams = WaykiCdpRedeemTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, sCoinsToRepay, map)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpRedeemTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
   * 清算cdp交易
   * Liquidate cdp transaction
   * fee Minimum 0.01 wicc
   * */
    @Test
    fun testLiquidateCdpTx() {
        val nValidHeight = 283308L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val sCoinsToLiquidate = 10000000L  //Liquidate amount
        val liquidateAssetSymbol = CoinType.WICC.type  //Asset symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val txParams = WaykiCdpLiquidateTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, sCoinsToLiquidate,liquidateAssetSymbol)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpLiquidateTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
  * Dex 限价买单交易
  * Dex limit price transaction
  * fee Minimum 0.001 wicc
  * */
    @Test
    fun testDexBuyLimitTx() {
        val nValidHeight = 283308L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_BUY_LIMIT_ORDER_TX  //限价买单
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val coinSymbol = CoinType.WUSD.type
        val assetSymbol = CoinType.WICC.type
        val assetAmount = 100 * 100000000L
        val bidPrice = 10 * 10000L
        val txParams = WaykiDexLimitTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol, coinSymbol, assetSymbol, assetAmount, bidPrice, txType)
        txParams.signTx(srcKey)
        val tx = wallet.createDexLimitTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
    * Dex 限价卖单交易
    * Dex limit sell price transaction
    * fee Minimum 0.001 wicc
   * */
    @Test
    fun testDexSellLimitTx() {
        val nValidHeight = 283308L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_SELL_LIMIT_ORDER_TX  //限价买单
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val coinSymbol = CoinType.WUSD.type
        val assetSymbol = CoinType.WICC.type
        val assetAmount = 100 * 100000000L
        val askPrice = 1 * 10000L
        val txParams = WaykiDexLimitTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol, coinSymbol, assetSymbol, assetAmount, askPrice, txType)
        txParams.signTx(srcKey)
        val tx = wallet.createDexLimitTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
     *  Dex 市价买单交易
     * Dex market buy price transaction
     * fee Minimum 0.001 wicc
    * */
    @Test
    fun testDexMarketBuyLimitTx() {
        val nValidHeight = 283308L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_BUY_MARKET_ORDER_TX  //市价买单
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val coinSymbol = CoinType.WUSD.type
        val assetSymbol = CoinType.WICC.type
        val assetAmount = 100 * 100000000L
        val txParams = WaykiDexMarketTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol, coinSymbol, assetSymbol, assetAmount, txType)
        txParams.signTx(srcKey)
        val tx = wallet.createDexMarketTransactionRaw(txParams)
        logger.info(tx)
    }


    /*
     *  Dex 市价卖单交易
     * Dex market sell price transaction
     * fee Minimum 0.001 wicc
    * */
    @Test
    fun testDexMarketSellLimitTx() {
        val nValidHeight = 283308L
        val fee = 10000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_SELL_MARKET_ORDER_TX //市价买单
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val coinSymbol = CoinType.WUSD.type
        val assetSymbol = CoinType.WICC.type
        val assetAmount = 100 * 100000000L
        val txParams = WaykiDexMarketTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol, coinSymbol, assetSymbol, assetAmount, txType)
        txParams.signTx(srcKey)
        val tx = wallet.createDexMarketTransactionRaw(txParams)
        logger.info(tx)
    }


    /*
    *  Dex 取消挂单交易
    * Dex cancel order tx
    * fee Minimum 0.001 wicc
   * */
    @Test
    fun testDexCancelOrderTx() {
        val nValidHeight = 283308L
        val fee = 1000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        println(userPubKey)
        val dexOrderId="009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //dex order tx hash
        val txParams = WaykiDexCancelOrderTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol,dexOrderId)
        txParams.signTx(srcKey)
        val tx = wallet.createDexCancelOrderTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
    * 资产发布
    * Asset release
    * symbol 大写字母A-Z 1-7 位 [A_Z]
    * Symbol Capital letter A-Z 1-7 digits [A_Z]
    * fee Minimum 0.01 wicc
    * account - 550 wicc
    * */
    @Test
    fun testCAssetIssueTx(){
        val nValidHeight = 11375L
        val fee = 1000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val symbol="STOOOOO"
        val asset=CAsset(symbol,"0-1","SS TOKEN",1000000000000000,true)
        val txParams = WaykiAssetIssueTxParams(nValidHeight, fee, userId,
                feeSymbol,asset)
        txParams.signTx(srcKey)
        val tx = wallet.createAssetIssueRaw(txParams)
        logger.info(tx)
    }

    /*
   * 资产发布
   * Asset Update
   * asset_symbol 大写字母A-Z 1-7 位 [A_Z]
   * Symbol Capital letter A-Z 1-7 digits [A_Z]
   * fee Minimum 0.01 wicc
   * account - 110 wicc
   * */
    @Test
    fun testCAssetUpdateTx(){
        val nValidHeight = 11443L
        val fee = 1000000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val asset=AssetUpdateData(AssetUpdateType.OWNER_UID,"0-2")  //update asset owner

       // val asset=AssetUpdateData(AssetUpdateType.NAME,"TestCoin") // update asset name

       //val asset=AssetUpdateData(AssetUpdateType.MINT_AMOUNT,200000000L) //update asset number
        val txParams = WaykiAssetUpdateTxParams(nValidHeight, fee, userId,
                feeSymbol,"STOOOOO",asset)
        txParams.signTx(srcKey)
        val tx = wallet.createAssetUpdateRaw(txParams)
        logger.info(tx)
    }


    /*
   * 合约发布交易
   * Deploy Contract transaction sample
   * fee Minimum 1.1 wicc
   * */
    @Test
    fun testDeployContractTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val regId = "0-1"
        val file=File("hello.lua")
        val contractByte = file.readBytes() ;
        val description = "description script"
        val txParams = WaykiDeployContractTxParams( 723575, 1100000000, regId,
                contractByte,  description)
        txParams.signTx(srcKey)
        val tx = wallet.createDeployContractRaw(txParams)
        logger.info(tx)
    }

    @Test
    fun testParseTransactionRaw() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        var rawtx = "0b0180cb4c21036c5397f3227a1e209952829d249b7ad0f615e43b763ac15e3a6f52627a10df21045749434383e1ac000114079b9296a00a2b655787fa90e66ec3cde4bf1c8c0457494343cd1006e8bdace8b4a647304502210097cfa3068593913894fceeddc724e0848fb7c2012d406e3d3f21eab9211d208702203bd0835017bccd054a3770d6c838925760cc3de70ac646919705192f7c160751"
        var ret = wallet.parseTransactionRaw(rawtx, netParams)
        logger.info(ret.toString())

        rawtx = "0201999c7d2102a722a3a94fb41d92bcf9d54cd76ea40c8b0c223d6f0570389b775120c5e487640083e1ac0046304402205304902f6ae8470e7c294b8abe7fdd5a9847d8980914234c9ddb9b6098e473d002200ad2d0238292285394447905cb20b7275cd2daf3a68d1237a1200982b99172bc"
        ret = wallet.parseTransactionRaw(rawtx, netParams)
        logger.info(ret.toString())

        rawtx = "0f01abb416020001049abf7f0102f001858c2004574943430457555344aed6c100473045022100c9bc3579329d6a63a96fce8271ff7c09700f82fbbefcba37f59cd6ddb2bf79b70220476864326646346e5d2a34f2b0e326f2a1ec4f4114ca3513d69d8fdea6d7471c"
        ret = wallet.parseTransactionRaw(rawtx, netParams)
        logger.info(ret.toString())
    }

}