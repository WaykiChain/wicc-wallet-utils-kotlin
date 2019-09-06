# wicc-wallet-utils

## 维基链离线钱包工具SDK (WaykiChain Offline Wallet Utilities SDK)

 * 维基链钱包工具库 (WaykiChain Offline Wallet Utils)
 * 开发语言 (Implementation Language)：Kotlin 
 
## 核心功能 (Core Functions)
* 维基币地址生成 (Key and address generation)
* 交易离线签名 (Offline Transaction Signing)
## 使用方式（Usage）
### 创建钱包（WaykiChain Create Wallet）
生成助记词和私钥管理你的钱包
Generate mnemonics and private keys to manage your wallet.

- [1.生成助记词 GenerateMnemonics. You will get 12 words](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestWallet.kt)
```kotlin
  var words = MnemonicUtil.randomMnemonicCodes()
```
- [2.生成钱包 generate wallet from mnemonic](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestWallet.kt)
```kotlin
  val words = "vote despair mind rescue crumble choice garden elite venture cattle oxygen voyage"
  val networkParameters = WaykiTestNetParams.instance //generate Testnet Address From Mnemonic
  //val networkParameters = WaykiMainNetParams.instance //generate Mainnet Address From Mnemonic
  val wallet= BIP44Util.generateWaykiWallet(words,networkParameters)
```
- [3.导入私钥 Import private key](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestWallet.kt)
```kotlin
 val params = WaykiTestNetParams.instance //TestNet
 val privKeyWiF = "YAHcraeGRDpvwBWVccV7NLGAU6uK39nNUTip8srbJSu6HKSTfDcC"
 val key = DumpedPrivateKey.fromBase58(params, privKeyWiF).key
```
### 交易签名（WaykiChain Sign Transaction）
使用私钥对交易进行签名，您可以通过Bass服务提交离线签名rawtx交易。

Signing a transaction with a private key,you can submit your offline signature rawtx transaction by bass service.

|  BassNetwork |  ApiAddr | 
|-------------- |----------------------------------|
|   TestNetwork | https://baas-test.wiccdev.org/v2/api/swagger-ui.html#!/  |  
|   ProdNetwork | https://baas.wiccdev.org/v2/api/swagger-ui.html#!/       |                                |

**提交交易 Submit raw string:**  
Mainnet <https://baas.wiccdev.org/v2/api/swagger-ui.html#!/transaction-controller/offlinTransactionUsingPOST> ,  
TestNet <https://baas-test.wiccdev.org/v2/api/swagger-ui.html#!/transaction-controller/offlinTransactionUsingPOST>  
**获得区块高度 Get block height:**  
MainNet<https://baas.wiccdev.org/v2/api/swagger-ui.html#!/block-controller/getBlockCountUsingPOST>,  
TestNet <https://baas-test.wiccdev.org/v2/api/swagger-ui.html#!/block-controller/getBlockCountUsingPOST>

#### WaykiChain Transaction
   [**WICC交易单位说明 (WICC Transaction Unit description)**](https://wicc-devbook.readthedocs.io/zh_CN/latest/Problem/question/)
- [1.钱包注册交易 （Sign Register Account Transaction）](https://githuCb.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)

**钱包注册交易已不是必须的，你可以在其他交易通过公钥激活你的钱包。  
(The register transaction is not required, you can activate wallet by public key in other transactions)**
```kotlin
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val privKeyWiF = "Y9XMqNzseQFSK32SvMDNF9J7xz1CQmHRsmY1hMYiqZyTck8pYae3"
        val key = DumpedPrivateKey.fromBase58(netParams, privKeyWiF).key
        val txParams = WaykiRegisterAccountTxParams(key.publicKeyAsHex, null, 429821, 10000, CoinType.WICC.type)
        txParams.signTx(key)
        val tx = wallet.createRegisterTransactionRaw(txParams)
```
- [2.钱包转账交易 （Sign Common Transaction）](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt).
```kotlin
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val pubKey = srcKey.publicKeyAsHex
        val destAddr = "wWTStcDL4gma6kPziyHhFGAP6xUzKpA5if"
        val memo="test transfer"
        val txParams = WaykiCommonTxParams(WaykiNetworkType.TEST_NET, 34550, pubKey,10000,1100000000000, "0-1", destAddr,memo)
        txParams.signTx(srcKey)
        val tx = wallet.createCommonTransactionRaw(txParams)
```
- [3.多币种转账交易 （Sign UCoinTransfer Transaction）](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance

        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val pubKey = srcKey.publicKeyAsHex  //user publickey hex string
        val nValidHeight = 440601L
        val coinSymbol = CoinType.WICC.type  //coind symbol
        val coinAmount = 100000000L    //transfer amount
        val feeSymbol = CoinType.WICC.type
        val fees = 100000L //Minimum  fee 10000sawi
        val regid = ""
        val destAddr = "wWXYkAhNdNdv5LBEavQB1aUJeYqApNc2YW"
        val memo = "转账"
        val txParams = WaykiUCoinTxParams(WaykiNetworkType.TEST_NET, nValidHeight, regid, pubKey, destAddr, coinSymbol, coinAmount, feeSymbol, fees, memo)
        txParams.signTx(srcKey)
        val tx = wallet.createUCoinTransactionRaw(txParams)
```
- [4.钱包投票交易 （Sign Delegate Transaction）](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance 
        val srcPrivKeyWiF = "Y6CFeJthSWMPRRcEu734u4ovBfjRp3ytngt9iGEfsMvqxPmKo2Vy"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val srcAddress = LegacyAddress.fromPubKeyHash(netParams, srcKey.pubKeyHash).toString()
        val votedPubKey=Utils.HEX.decode("2ba8329bc5507c867bdc9be0ce487419de3c6737ae6754657db62f2df02ff07f")//public key as hex string
        //VoteOperType.ADD_FUND  投票
        //VoteOperType.MINUS_FUND //撤销投票
        val array1 = OperVoteFund(VoteOperType.ADD_FUND.value, votedPubKey, 200000000)
        val array2 = arrayOf(array1)
        val txParams = WaykiDelegateTxParams("25813-1",srcKey.publicKeyAsHex, array2, 10000000, 479796)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)
```
- [5.调用合约交易 （Sign Invoke Contract Transaction）](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)  

     [**WRC20 Asset Invoke**](https://www.wiccdev.org/book/zh-hans/Contract/ico_sample.html#%E5%AF%B9%E4%BB%A3%E5%B8%81%E8%BF%9B%E8%A1%8C%E8%BD%AC%E8%B4%A6)
````kotlin
        //Activate WRC20 Assets
        //激活WRC20资产
        val wallet = LegacyWallet()
        val netParams = WaykiMainNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val regId = "926152-1"
        val appId = "128711-1"
        val contract = "f0110000"
        val contractByte = ContractUtil.hexString2binaryString(contract)
        val txParams = WaykiContractTxParams(srcKey.publicKeyAsHex, 494454, 100000, 0, regId, appid, contractByte, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createContractTransactionRaw(txParams)
````
   
````kotlin
        //WRC20 Transfer
        //WRC20转账
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).key
        val regId = "926152-1"
        val appId = "128711-1" //Contract Regid
        val wrc20Amount = 10000L // transfer 10000 WRC Token
        val destAddress="wNPWqv9bvFCnMm1ddiQdH7fUwUk2Qgrs2N"
        val contractByte = ContractUtil.transferWRC20Contract(wrc20Amount,destAddress)
        val txParams = WaykiContractTxParams(srcKey.publicKeyAsHex, 494454, 100000, 0, regId, appId, contractByte, CoinType.WICC.type)
        txParams.signTx(srcKey)
        val tx = wallet.createContractTransactionRaw(txParams)
````
#### CDP Transaction
持有WICC的任何用户都可以向CDP（抵押债务位置）发送WICC以获得一定百分比的WUSD.一个用户只能拥有一个CDP，除非之前的CDP已被销毁。

Any user holding a WICC can send a WICC to the CDP (Collaterized Debt Position) to obtain a certain percentage of WUSD.a user can only have one cdp unless the previous cdp has been destroyed.

- [CDP抵押交易签名 (Sign Cdp Stake Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
````kotlin
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
````
- [CDP赎回交易签名 (Sign Cdp Redeem Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
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
```
- [CDP清算交易 (Sign CDP Liquidate Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
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
```
#### DEX Transaction
维基链去中心化交易所 (WaykiChain decentralized exchange).
- [限价卖单交易签名 (Sign Dex Sell Limit Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt) 
```kotlin
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_SELL_LIMIT_ORDER_TX  //限价卖单
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
```
- [限价买单交易签名 (Sign Dex Buy Limit Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
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
```
- [市价卖单交易签名 (Sign Dex Market Sell Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
        val nValidHeight = 283308L
        val fee = 100000L
        val userId = "0-1" //wallet regid
        val feeSymbol = CoinType.WICC.type  //fee symbol
        val wallet = LegacyWallet()
        val netParams = WaykiTestNetParams.instance
        val srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13"
        val txType = WaykiTxType.DEX_SELL_MARKET_ORDER_TX //市价卖单
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
```
- [市价买单交易签名 (Sign Dex Market Buy Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
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
```
- [取消挂单交易签名 (Sign Dex Cancel Transaction)](https://github.com/WaykiChain/wicc-wallet-utils-kotlin/blob/master/src/test/kotlin/com/waykichain/wallet/TestTransaction.kt)
```kotlin
        val nValidHeight = 283308L
        val fee = 100000L
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
```
## 如何编译打包 (How to build)
* 执行命令 (Execution Command)
```
gradle jar -PwiccBuildJar
```

* 输出(output): 

```build/libs/wicc-wallet-utils-2.0.0.jar```

## 参考三方项目 (Reference Projects)
* https://bitcoinj.github.io/
* https://github.com/bitcoin/secp256k1