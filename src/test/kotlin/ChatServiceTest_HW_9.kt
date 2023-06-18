import org.junit.Test

import org.junit.Assert.*

class ChatServiceTestHW9 {

    @Test
    fun createMsgShouldCreate() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        val msg = chat.getMsgOfChatFunc(1, 4)
        val result = msg.isNotEmpty()
        assertTrue(result)
    }

    @Test
    fun deleteChatShouldDelete() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteChat(1, 1)
        val chats = chat.getAllChats(1)
        val result = chats.isEmpty()
        assertTrue(result)
    }

    @Test(expected = ChatService.ChatNotFoundException::class)
    fun deleteChatShouldThrowException() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteChat(1, 2)
    }

    @Test
    fun deleteMsdShouldDelete() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteMsd(1, 4, 1)
        val msg = chat.getMsgOfChatFunc(1, 4)
        val result = msg.isEmpty()
        assertTrue(result)
    }

    @Test(expected = ChatService.MessageNotFoundException::class)
    fun deleteMsdShouldThrowException() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        chat.deleteMsd(1, 4, 2)
    }

    @Test
    fun readMsgShouldRead() {
        val chat = ChatService()
        chat.createMsg(1, 4, "msg1.1")
        val result = chat.readMsg(1, 4, 1)
        assertTrue(result)
    }

}