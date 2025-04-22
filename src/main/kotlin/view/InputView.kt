package view

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object InputView {
    suspend fun inputKeyword(): String? {
        val keyward: String
        withContext(Dispatchers.IO) {
            println("검색어를 입력하세요 (없으면 전체 출력): ")
            keyward = readln()
        }
        return keyward
    }
}
