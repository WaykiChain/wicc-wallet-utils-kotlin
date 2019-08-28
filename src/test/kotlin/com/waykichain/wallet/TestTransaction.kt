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
import java.util.*

class TestTransaction {
    private val logger = LoggerFactory.getLogger(javaClass)
    /*
    * 账户注册交易,新版本已基本废弃，可改用公钥注册，免注册费用
    * Account registration transaction, the new version has been abandoned, you can use public key registration, free registration fee
    * */
    @Test
    fun testGenerateRegisterAccountTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val privKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"

        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        logger.info("            ${key.publicKeyAsHex}")

        val txParams = WaykiRegisterAccountTxParams(key.publicKeyAsHex, null, 429821, 10000, CoinType.WICC.type)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")

    }

    /*
    * 测试网转账交易
    * Test network transfer
    * */
    @Test
    fun testGenerateCommonTxForTestNet() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()

        val destAddr = "wWXYkAhNdNdv5LBEavQB1aUJeYqApNc2YW"

        val txParams = WaykiCommonTxParams(WaykiNetworkType.TEST_NET, 429637, 100660, 100000000, "423318-1", destAddr, CoinType.WICC.type)//"30947-1", destAddr)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
    }

    /*
    * 主网转账交易
    * Main network transfer
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


        val destAddr = "WQRwCMmQGy2XvpATTai6AtGhrRrdXDQzQh"
        val txParams = WaykiCommonTxParams(WaykiNetworkType.MAIN_NET, 1926165, 10000, 10000, "926152-1", destAddr, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
        logger.info("${tx.length} - $tx")
    }


    /*
   * 多币种转账交易 ,支持多种币种转账
   * Test nUniversal Coin Transfer Tx
   * */
    @Test
    fun testGenerateUCoinTransferTx() {
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val pubKey = srcKey.publicKeyAsHex  //user publickey hex string
        val nValidHeight = 283308L
        val coinSymbol = CoinType.WICC.type  //coind symbol
        val coinAmount = 100000000L    //transfer amount
        val feeSymbol = CoinType.WICC.type
        val fees = 100000L
        val regid = "0-1"
        val destAddr = "wWXYkAhNdNdv5LBEavQB1aUJeYqApNc2YW"
        val memo = "转账"
        val txParams = WaykiUCoinTxParams(WaykiNetworkType.TEST_NET, nValidHeight, regid, pubKey, destAddr, coinSymbol, coinAmount, feeSymbol, fees, memo)
        txParams.signTx(srcKey)
        val tx = wallet.createUCoinTransactionRaw(txParams)
        logger.info("$tx")
    }

    /*
    * 投票交易
    * Voting transaction
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
        val txParams = WaykiDelegateTxParams("25813-1", array6, 10000000, 479796, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
    * 合约交易
    * Contract transaction sample
    * */
    @Test
    fun testGenerateContractTx() {
        //以锁仓为例 锁仓90天
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance

        val srcPrivKeyWiF = "PhKmEa3M6BJERHdStG7nApRwURDnN3W48rhrnnM1fVKbLs3jaYd6"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        logger.info(LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString())

        val value = 100000000L //锁仓一个WICC
        val header = "f202" //需要调用的方法
        val appid = "450687-1"//合约锁仓90天合约的ID
        val contract = header + ContractUtil.to2HexString4byte(value) + "00000000"
        logger.info(contract)
        val contractByte = ContractUtil.hexString2binaryString(contract)
        val txParams = WaykiContractTxParams(srcKey.publicKeyAsHex, 494454, 100000, value, "926152-1", appid, contractByte, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createContractTransactionRaw(txParams)
        logger.info(tx)
    }


    /*
    * 创建,追加cdp交易
    * Create or append an  cdp transaction
    * fee Minimum 0.001 wicc
    * */
    @Test
    fun testGenerateCdpStakeTx() {
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WICC.type  //fee symbol
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
        val txParams = WaykiCdpStakeTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, bCoinSymbol, sCoinSymbol, bCoinToStake, sCoinToMint)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpStakeTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
   * 赎回cdp交易
   * Redeem cdp transaction
   * fee Minimum 0.001 wicc
   * */
    @Test
    fun testRedeemCdpTx() {
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val sCoinsToRepay = 50000000L  //repay amount
        val bCoinsToRedeem = 100000000L   //redeem amount

        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val txParams = WaykiCdpRedeemTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, sCoinsToRepay, bCoinsToRedeem)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpRedeemTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
   * 清算cdp交易
   * Liquidate cdp transaction
   * fee Minimum 0.001 wicc
   * */
    @Test
    fun testLiquidateCdpTx() {
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val cdpTxid = "009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //wallet cdp create tx hash
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val sCoinsToLiquidate = 10000000L  //Liquidate amount

        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val txParams = WaykiCdpLiquidateTxParams(nValidHeight, fee, userId, userPubKey, cdpTxid, feeSymbol, sCoinsToLiquidate)
        txParams.signTx(srcKey)
        val tx = wallet.createCdpLiquidateTransactionRaw(txParams)
        logger.info(tx)
    }

    /*
  * Dex 限价买单交易
  * Dex limit price transaction
  * fee Minimum 0.0001 wicc
  * */
    @Test
    fun testDexBuyLimitTx() {
        val nValidHeight = 283308L
        val fee = 100000L
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
    * fee Minimum 0.0001 wicc
   * */
    @Test
    fun testDexSellLimitTx() {
        val nValidHeight = 283308L
        val fee = 100000L
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
     * fee Minimum 0.0001 wicc
    * */
    @Test
    fun testDexMarketBuyLimitTx() {
        val nValidHeight = 283308L
        val fee = 100000L
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
     * fee Minimum 0.0001 wicc
    * */
    @Test
    fun testDexMarketSellLimitTx() {
        val nValidHeight = 283308L
        val fee = 100000L
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
    * fee Minimum 0.0001 wicc
   * */
    @Test
    fun testDexCancelOrderTx() {
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        //if no wallet regid ,you can use wallet public key
        val userPubKey = srcKey.publicKeyAsHex //wallet publickey hex string
        val dexOrderId="009c0e665acdd9e8ae754f9a51337b85bb8996980a93d6175b61edccd3cdc144" //dex order tx hash
        val txParams = WaykiDexCancelOrderTxParams(nValidHeight, fee, userId, userPubKey,
                feeSymbol,dexOrderId)
        txParams.signTx(srcKey)
        val tx = wallet.createDexCancelOrderTransactionRaw(txParams)
        logger.info(tx)
    }

}