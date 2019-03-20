package com.waykichain.wallet

import com.waykichain.wallet.base.OperVoteFund
import com.waykichain.wallet.base.VoteOperType
import com.waykichain.wallet.base.params.WaykiDelegateTxParams
import com.waykichain.wallet.impl.LegacyWallet
import org.bitcoinj.core.ECKey
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.junit.Test
import org.slf4j.LoggerFactory
import org.waykichain.wallet.util.BIP44Util
import org.waykichain.wallet.util.MnemonicUtil
import java.util.*

class RunWallet {
    private val logger = LoggerFactory.getLogger(javaClass)


    @Test
    fun runGenerateVoteDelegateTx() {
        val wallet = LegacyWallet()
        val coinUnit = 100000000L
        val srcRegId = "2235518-1"
        val fees = 10000L

        val voteAmount = 3530000 * coinUnit
        val validHeight = 2322670L


        val delegatePubKeyList = arrayOf(
                "02d5b0c28802250ff0e48ba1961b79337b1ed4c2a7e695f5b0b41c6777b1bd2bcf",   //0-2
                "02e829790a0bcfc5b62547a38ef2880dd653df61d52d1523d22e0d5431fc0bae3b",   //0-3
                "03145134d18bbcb1da64adb201d1234f57b3daee8bb7d0bcbe7c27b53edadcab59",   //0-4
                "021254f99a9228d368fe8d4493c7f60ab1a63ce6e830c5582e2fc46a6f8abf6ae6",   //2023224-1
                "023072f83d35cd987bd8d4394d70d08671344d56c3231bd7c58f03a78037b09d58",   //2022737-1
                "0350971adb0b339b0f9c85ec9d89423fb46173fcc5e3ec6e6e556c6d0d7beff366",   //2023263-1
                "02d26ebccfe0017105bfb0964995d63dcb2327da80422074eb48de5495f2278144",   //2023062-1
                "0273a95881c340f6dbe569bcf7001ac476200ce4f908bd246957d8ceff8a44fdd8",   //2023379-1
                "036d16cd0fca8045369d29a0dc8ef809aee19697da0eb00b410f40fbd2bd9816ca",   //2023145-1
                "0272c15e10b3bff2b7024dc8e2b0ee9a30339bda6dc11b71018e534779d113583b",   //2023408-1
                "03c114be6d5241cf8daf676ad1c172bb5086e15529fba90e9bea1dea5fe7e5a9d7" )  //2023425-1


        val srcKey = getMainnetEcKeyFromMnecode()

        val voteArr = arrayListOf<OperVoteFund>()
        for ( pubKeyHex in delegatePubKeyList) {
            val pubKey = decodeHexString(pubKeyHex)
            val vote = OperVoteFund(VoteOperType.ADD_FUND.value, pubKey, voteAmount)
            voteArr.add(vote)
        }

        val txParams = WaykiDelegateTxParams(srcRegId, voteArr.toTypedArray(), fees, validHeight)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)

        logger.info(tx)
    }

    private fun getMainnetEcKeyFromMnecode(): ECKey {
        System.out.println("Please enter 12-word mnemonic phrase below: \n")
        val words = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: {$words}")

        val wordList = words.split(", ")
        MnemonicUtil.validateMnemonics(wordList)

        val seed = DeterministicSeed(wordList, null, "", 0L)
        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
        val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
        return ECKey.fromPrivate(mainKey.privKey)
    }

    fun decodeHexString(hexString: String): ByteArray {
        if (hexString.length % 2 == 1)
            throw IllegalArgumentException("Invalid hexadecimal String supplied.")

        val bytes = ByteArray(hexString.length / 2)
        var i = 0
        while (i < hexString.length) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2))
            i += 2
        }
        return bytes
    }

    fun hexToByte(hexString: String): Byte {
        val firstDigit = toDigit(hexString[0])
        val secondDigit = toDigit(hexString[1])
        return ((firstDigit shl 4) + secondDigit).toByte()
    }

    private fun toDigit(hexChar: Char): Int {
        val digit = Character.digit(hexChar, 16)
        if (digit == -1) {
            throw IllegalArgumentException(
                    "Invalid Hexadecimal Character: $hexChar")
        }
        return digit
    }
}