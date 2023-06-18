class ChatService {
    data class Chat(
        val ownerId: Int = 0,
        val chatId: Int = 0,
        val toWhoId: Int = 0,
        val inMsg: MutableList<Message> = mutableListOf(),
        val outMsg: MutableList<Message> = mutableListOf()
    )

    data class Message(
        val ownerId: Int = 1,
        val toWhoId: Int = 0,
        val msgId: Int = 0,
        val text: String = "",
        var readMark: Boolean = false
    )

    private var chats = mutableListOf<Chat>()
    private var msgId: Int = 1
    private var chatId: Int = 0


    class ChatNotFoundException(message: String) : RuntimeException(message)
    class MessageNotFoundException(message: String) : RuntimeException(message)

//    fun createMsg(ownerId: Int, toWhoId: Int, text: String): Int {
//        val message = Message(ownerId = ownerId, msgId = msgId++, toWhoId = toWhoId, text = text)
//        chatId++
//
//        // проверка наличия чата у отправителя: создаю или просто добавляю смс
//        val chat1 = chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
//        if (chat1 == null) {
//            chats += Chat(ownerId = ownerId, chatId = chatId, toWhoId = toWhoId, outMsg = mutableListOf(message))
//        } else chat1.outMsg += message
//
//        // проверка наличия чата у получателя: создаю или просто добавляю смс
//        val chat2 = chats.find { it.ownerId == toWhoId && it.toWhoId == ownerId }
//        if (chat2 == null) {
//            chats += Chat(ownerId = toWhoId, chatId = chatId, toWhoId = ownerId, inMsg = mutableListOf(message))
//        } else chat2.inMsg += message
//
//        return 1
//    }

    // вынесла проверку и создание чата в отд ф-цию
    private fun checkCreateChat(ownerId: Int, toWhoId: Int, message: Message) {
        chatId++
        // проверка наличия чата у отправителя: создаю или просто добавляю смс
        val chat1 = chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
        if (chat1 == null) {
            chats += Chat(ownerId = ownerId, chatId = chatId, toWhoId = toWhoId, outMsg = mutableListOf(message))
        } else chat1.outMsg += message

        // проверка наличия чата у получателя: создаю или просто добавляю смс
        val chat2 = chats.find { it.ownerId == toWhoId && it.toWhoId == ownerId }
        if (chat2 == null) {
            chats += Chat(ownerId = toWhoId, chatId = chatId, toWhoId = ownerId, inMsg = mutableListOf(message))
        } else chat2.inMsg += message
    }

    fun createMsg(ownerId: Int, toWhoId: Int, text: String): Int {
        val message = Message(ownerId = ownerId, msgId = msgId++, toWhoId = toWhoId, text = text)
        checkCreateChat(ownerId, toWhoId, message)
        return 1
    }


    fun getAllChats(ownerId: Int): List<Chat> {
        return chats.filter { it.ownerId == ownerId }.sortedBy { it.chatId }
    }

//    fun getMsgOfChat(ownerId: Int, toWhoId: Int): String {
//        val chat = chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
//        chat ?: throw ChatNotFoundException("Chat not found")
//        val allMsg = chat.inMsg + chat.outMsg
//        return allMsg.sortedBy { it.msgId }.joinToString("")
//    }

    // От эксперта :
    fun getMsgOfChatFunc(ownerId: Int, toWhoId: Int) =
        chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            ?.let { it.inMsg + it.outMsg }
            ?.sortedBy { it.msgId }
        //            ?.joinToString("\n")
            ?: throw ChatNotFoundException("Chat not found")

    fun deleteChat(ownerId: Int, chatId: Int): Int {
        val chat = chats.filter { it.ownerId == ownerId }.find { it.chatId == chatId }
        chat ?: throw ChatNotFoundException("Chat not found")
        chats.remove(chat)
        return 1
    }

    fun deleteMsd(ownerId: Int, toWhoId: Int, msgId: Int): Int {
        val chat = chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
        chat ?: throw MessageNotFoundException("Message not found")
        val msg = chat.outMsg.find { it.msgId == msgId }
        msg ?: throw MessageNotFoundException("Message not found")
        chat.outMsg.remove(msg)
        return 1
    }

    fun readMsg(ownerId: Int, toWhoId: Int, msgId: Int): Boolean {
        var mark = false
        // метка прочитанные в чате отправителя
        val chatInd = chats.indices.find { chats[it].ownerId == ownerId && chats[it].toWhoId == toWhoId }
        if (chatInd != null) {
            val outMsgInd = chats[chatInd].outMsg.indices.find { chats[chatInd].outMsg[it].msgId == msgId }
            if (outMsgInd != null) {
                chats[chatInd].outMsg[outMsgInd].readMark = true
                mark = true
            } else println("No such message")
        } else println("No such message")

        // метка прочитанные в чате получателя
        val chat2Ind = chats.indices.find { chats[it].ownerId == toWhoId && chats[it].toWhoId == ownerId }
        if (chat2Ind != null) {
            val inMsgInd = chats[chat2Ind].inMsg.indices.find { chats[chat2Ind].inMsg[it].msgId == msgId }
            if (inMsgInd != null) {
                chats[chat2Ind].inMsg[inMsgInd].readMark = true
                mark = true
            } else println("No such message")
        } else println("No such message")

        return mark
    }

    fun getUnreadMsgOfChat(ownerId: Int, toWhoId: Int): String {
        val chat = chats.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
        chat ?: throw ChatNotFoundException("Chat not found")
        val unreadMsg = chat.inMsg.filter { !it.readMark }
        return if (unreadMsg.isEmpty()) {
            "Нет непрочитанных сообщений в чате с $toWhoId"
        } else
            "Непрочитанные сообщения в чате с $toWhoId: " + unreadMsg.sortedBy { it.msgId }.joinToString("")
    }

    fun getUnreadMsgOfAllChats(ownerId: Int): String {
        val chat = chats.filter { it.ownerId == ownerId }
        val sb: StringBuilder = StringBuilder()
        chat.forEach {
            val owner = it.ownerId
            val toWho = it.toWhoId
            sb.append(getUnreadMsgOfChat(owner, toWho))
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun unreadChat(chat: Chat): Boolean {
        val inMsg = chat.inMsg
        val notRead = inMsg.filter { !it.readMark }
        if (notRead.isEmpty()) {
            return true
        }
        return false
    }

    fun getUnreadChats(ownerId: Int): String {
        val chats = chats.filter { it.ownerId == ownerId }
        return chats.filter { chat: Chat -> !unreadChat(chat) }.joinToString("\n")
    }

    fun getUnreadChatsCount(ownerId: Int): Int {
        val chats = chats.filter { it.ownerId == ownerId }
        val unreadChats = chats.filter { chat: Chat -> !unreadChat(chat) }
        return unreadChats.size
    }

    fun getSpecifiedInMsg(ownerId: Int, chatId: Int, msgId: Int, numberToShow: Int): String {
        val chat = chats.indices.find { chats[it].ownerId == ownerId && chats[it].chatId == chatId }
        chat ?: throw ChatNotFoundException("Chat not found")
        val msg = (chats[chat].inMsg + chats[chat].outMsg).sortedBy { it.msgId }
        val ind = msg.indices.find { msg[it].msgId == msgId }
        ind ?: throw MessageNotFoundException("Message not found")
        val lastIndex =
            if ((ind + numberToShow) > (chats[chat].inMsg + chats[chat].outMsg).size) (chats[chat].inMsg + chats[chat].outMsg).size else (ind + numberToShow)
        for (index in ind until lastIndex) {
            if (chats[chat].inMsg.contains(msg[index])) {
                (chats[chat].inMsg + chats[chat].outMsg).sortedBy { it.msgId }[index].readMark = true
            }
        }
        val msgToShow =
            (chats[chat].inMsg + chats[chat].outMsg).sortedBy { it.msgId }.subList(fromIndex = ind, toIndex = lastIndex)
        return msgToShow.joinToString("\n")
    }
}


fun main() {
    val chat1 = ChatService()
    println(chat1.createMsg(1, 4, "msg1.1"))  // msdId = 1 (будет прочитано)
    println(chat1.createMsg(1, 4, "msg1.2"))  // msdId = 2
    println(chat1.createMsg(1, 6, "msg1.3"))  // msdId = 3  (будет прочитано)
    println(chat1.createMsg(4, 1, "msg4.1"))  // msdId = 4
    println(chat1.createMsg(4, 6, "msg5.2"))  // msdId = 5
    println(chat1.createMsg(4, 1, "msg4.2"))
    println(chat1.getAllChats(1))
    println(chat1.getAllChats(4))
    println(chat1.getAllChats(6))
    println()

    println("Specified in-msg")
    println(chat1.getSpecifiedInMsg(4, 1, 1, 3))

    println()
    println("reading msg")
    println(chat1.readMsg(1, 4, 1))
    println(chat1.readMsg(1, 6, 3))

    println()
    println("Unread msg:")
    println(chat1.getUnreadMsgOfChat(1, 4)) // + Message(ownerId=4, toWhoId=1, msgId=4, text=msg4.1, readMark=false)
    println(chat1.getUnreadMsgOfChat(4, 1))
    // + 1е смс - почитано, 2е прочитано (specified msg)
    println(chat1.getUnreadMsgOfChat(1, 6)) // + не писал вообще
    println(chat1.getUnreadMsgOfChat(6, 1)) // + прочитано
    println(chat1.getUnreadMsgOfChat(6, 4)) // + Message(ownerId=4, toWhoId=6, msgId=5, text=msg5.2, readMark=false)
    println(chat1.getUnreadMsgOfChat(4, 6)) // + не писал вообще
    println()
    println("Unread msg of ALL chats для 1")
    println(chat1.getUnreadMsgOfAllChats(1))
    println()
    println("Unread Chats")
    println(chat1.getUnreadChats(6))
    println(chat1.getUnreadChatsCount(6))
    println()

    println("Msg of Chat")
    println(chat1.getMsgOfChatFunc(1, 4))
    println(chat1.getMsgOfChatFunc(1, 6))
    println(chat1.getMsgOfChatFunc(4, 1))
    println(chat1.getMsgOfChatFunc(4, 6))
    println()
    println("Deleting Msg")
    println(chat1.deleteMsd(1, 4, 1))
    println(chat1.getAllChats(1))
    println()
    println("Deleting Chat")
    println(chat1.deleteChat(1, 1))
    println(chat1.getAllChats(1))
    println(chat1.getAllChats(4))
    println()


}