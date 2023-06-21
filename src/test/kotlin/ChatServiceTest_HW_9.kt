import org.junit.Test

import org.junit.Assert.*

class ChatServiceOptimTestHW9 {

    @Test
    fun createMsgShouldCreate() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        val msg = chat.getMsgOfChatFunc(1, 4)
        val result = msg.isNotEmpty()
        assertTrue(result)
    }

    @Test
    fun deleteChatShouldDelete() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteChat(1, 1)
        val chats = chat.getAllChats(1)
        val result = chats.isEmpty()
        assertTrue(result)
    }

    @Test(expected = ChatServiceOptimised.ChatNotFoundException::class)
    fun deleteChatShouldThrowException() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteChat(1, 2)
    }

    @Test
    fun deleteMsgShouldDelete() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteMsg(1, 4, 1)
        val msg = chat.getMsgOfChatFunc(1, 4)
        val result = msg.isEmpty()
        assertTrue(result)
    }

    @Test(expected = ChatServiceOptimised.MessageNotFoundException::class)
    fun deleteMsgShouldThrowException() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteMsg(1, 4, 2)
    }

    @Test
    fun readMsgShouldRead() {
        val chat = ChatServiceOptimised()
        chat.createMsg(1, 4, "msg1.1")
        val result = chat.readMsg(1, 4, 1)
        assertTrue(result)
    }

}