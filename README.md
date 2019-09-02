# wicc-wallet-utils

## 维基链离线钱包工具SDK (WaykiChain Offline Wallet Utilities SDK)

 * 维基链钱包工具库 (WaykiChain Offline Wallet Utils)
 * 开发语言 (Implementation Language)：Kotlin 
 
## 核心功能 (Core Functions)
* 维基币地址生成 (Key and address generation)
* 交易离线签名 (Offline Transaction Signing)

## 如何编译打包 (How to build)
* 执行命令 (Execution Command)
```
gradle jar -PallInOne
```

* 输出(output): 

```build/libs/wicc-wallet-utils-2.0.0.jar```

## 运行参数设置 (Deprecated)

* VM options： (Deprecated since C++ library is no longer used) 
```
-Djava.library.path=/Users/richardchen/dev/src/github-src/wicc-wallet-utils/src/main/jniLibs
```

## 参考三方项目 (Reference Projects)
* https://bitcoinj.github.io/
* https://github.com/bitcoin/secp256k1

