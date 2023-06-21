class ChatServiceOptimised {
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
    private var chatsSeq = chats.asSequence()

    private var msgId: Int = 1
    private var chatId: Int = 0


    class ChatNotFoundException(message: String) : RuntimeException(message)
    class MessageNotFoundException(message: String) : RuntimeException(message)

    // п.7
    private fun checkCreateChat(ownerId: Int, toWhoId: Int, message: Message) {
        chatId++
        // проверка наличия чата у отправителя: создаю или просто добавляю смс
        chatsSeq.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            .let {
                it?.outMsg?.plusAssign(message) ?: it.let {
                    chatsSeq += Chat(
                        ownerId = ownerId,
                        chatId = chatId,
                        toWhoId = toWhoId,
                        outMsg = mutableListOf(message)
                    )
                }
            }

        // проверка наличия чата у получателя: создаю или просто добавляю смс
        chatsSeq.find { it.ownerId == toWhoId && it.toWhoId == ownerId }

            .let {
                it?.inMsg?.plusAssign(message) ?: it.let {
                    chatsSeq += Chat(
                        ownerId = toWhoId,
                        chatId = chatId,
                        toWhoId = ownerId,
                        inMsg = mutableListOf(message)
                    )
                }
            }
    }

    //п.5
    fun createMsg(ownerId: Int, toWhoId: Int, text: String): Int {
        val message = Message(ownerId = ownerId, msgId = msgId++, toWhoId = toWhoId, text = text)
        checkCreateChat(ownerId, toWhoId, message)
        return 1
    }

    // п.2
    fun getAllChats(ownerId: Int): List<Chat> {
        return chatsSeq.filter { it.ownerId == ownerId }
            .sortedBy { it.chatId }
            .toList()
    }

    // доп
    fun getMsgOfChatFunc(ownerId: Int, toWhoId: Int) =
        chatsSeq.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            ?.let { it.inMsg + it.outMsg }
            ?.sortedBy { it.msgId }
            ?.joinToString("\n")
            ?: throw ChatNotFoundException("Chat not found")

    // п.8
    fun deleteChat(ownerId: Int, chatId: Int): Int {
        chatsSeq.filter { it.ownerId == ownerId }
            .find { it.chatId == chatId }
            .let { chatsSeq -= it ?: throw ChatNotFoundException("Chat not found") }
        return 1
    }

    // п.6
    fun deleteMsg(ownerId: Int, toWhoId: Int, msgId: Int): Int {
        chatsSeq.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            .let { chat ->
                chat?.outMsg?.find { it.msgId == msgId }?.let { chat.outMsg.remove(it) }
                    ?: throw MessageNotFoundException("Message not found")
            }
        return 1
    }

    //доп
    fun readMsg(ownerId: Int, toWhoId: Int, msgId: Int): Boolean {
        var mark = false
        // метка прочитанные в чате отправителя
        chatsSeq.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            .let { it ->
                it?.outMsg?.find { it.msgId == msgId }?.let {
                    it.readMark = true
                    mark = true
                }
                    ?: println("No such message")
            }

        // метка прочитанные в чате получателя
        chatsSeq.find { it.ownerId == toWhoId && it.toWhoId == ownerId }
            .let { it ->
                it?.inMsg?.find { it.msgId == msgId }?.let {
                    it.readMark = true
                    mark = true
                }
                    ?: println("No such message")
            }

        return mark
    }

    // доп
    fun getUnreadMsgOfChat(ownerId: Int, toWhoId: Int) =
        chatsSeq.find { it.ownerId == ownerId && it.toWhoId == toWhoId }
            .let { it -> it?.inMsg?.filter { !it.readMark } }
            ?.sortedBy { it.msgId }
            ?.joinToString("\n") { "unread msg with $toWhoId: $it" }
            ?.ifEmpty { "No unread msg with $toWhoId" }

    //доп
    fun getUnreadMsgOfAllChats(ownerId: Int) =
        chatsSeq.filter { it.ownerId == ownerId }
            .map { chat -> getUnreadMsgOfChat(chat.ownerId, chat.toWhoId) }
            .joinToString("\n")

    private fun unreadChat(chat: Chat): Boolean {
        chatsSeq.find { it == chat }
            ?.let { ch -> ch.inMsg.filter { !it.readMark } }
            ?.ifEmpty { return true }
        return false
    }

    // доп к п.1
    fun getUnreadChats(ownerId: Int): String =
        chatsSeq.filter { it.ownerId == ownerId }
            .filter { chat: Chat -> !unreadChat(chat) }
            .joinToString("\n")

    // п.1
    fun getUnreadChatsCount(ownerId: Int): Int =
        chatsSeq.filter { it.ownerId == ownerId }
            .filter { chat: Chat -> !unreadChat(chat) }
            .count()

    // п.4
    fun getSpecifiedInMsg(ownerId: Int, chatId: Int, msgId: Int, numberToShow: Int) =

        chatsSeq.find { it.ownerId == ownerId && it.chatId == chatId }
            ?.let { it.inMsg + it.outMsg }
            ?.asSequence()
            ?.sortedBy { it.msgId }
            ?.filter { it.msgId >= msgId }
            ?.take(numberToShow)
            ?.onEach {
                if (it.toWhoId == ownerId) {
                    it.readMark = true
                }
            }
            ?.joinToString("\n")

    // п.3
    fun getLastMsgFunc(ownerId: Int) =
        chatsSeq.filter { it.ownerId == ownerId }
            .map { chat ->
                chat.let { it.inMsg + it.outMsg }
                    .maxByOrNull { it.msgId }
                    ?.text ?: "Нет сообщений"
            }
            .joinToString("\n")

}


fun main() {
    val chat1 = ChatServiceOptimised()
    println(chat1.createMsg(1, 4, "msg1.1"))  // msdId = 1 (будет прочитано)
    println(chat1.createMsg(1, 4, "msg1.2"))  // msdId = 2
    println(chat1.createMsg(1, 6, "msg1.3"))  // msdId = 3  (будет прочитано)
    println(chat1.createMsg(4, 1, "msg4.1"))  // msdId = 4
    println(chat1.createMsg(4, 6, "msg5.2"))  // msdId = 5
    println(chat1.createMsg(4, 1, "msg4.2"))
    println(chat1.createMsg(6, 7, "msg6.1"))
    println(chat1.deleteMsg(6, 7, 7))
    println(chat1.getAllChats(1))
    println(chat1.getAllChats(4))
    println(chat1.getAllChats(6))
    println()

    println("Specified in-msg")
    println(chat1.getSpecifiedInMsg(4, 1, 2, 2))
    println("All msg")
    println(chat1.getMsgOfChatFunc(4, 1))

    println()
    println("reading msg")
    println(chat1.readMsg(1, 4, 1))
    println(chat1.readMsg(1, 6, 3))
    println(chat1.getAllChats(4))

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
    println(chat1.deleteMsg(1, 4, 1))
    println(chat1.getAllChats(1))
    println()
    println("Deleting Chat")
    println(chat1.deleteChat(1, 1))
    println(chat1.getAllChats(1))
    println(chat1.getAllChats(4))
    println()
    println("Get last msg")
    println(chat1.getLastMsgFunc(6))


}