import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

class MyBot : TelegramLongPollingBot() {

    override fun getBotToken(): String {
        return "5891958593:AAGy2FM22EQTcj2mkzEASO2Ow0zu6HmW_J0"
    }

    override fun getBotUsername(): String {
        return "tgtiktokdeletebot"
    }

    override fun onUpdateReceived(update: Update?) {
        if (update?.hasMessage() == true) {
            val message: Message = update.message

            val wordsToDelete = listOf("vm.tiktok.com")

            val text = message.text?.lowercase()

            if (text != null && wordsToDelete.any { word -> text.contains(word) }) {
                val deleteMessage = DeleteMessage()
                deleteMessage.chatId = message.chatId.toString()
                deleteMessage.messageId = message.messageId
                try {
                    val senderName = getSenderName(message.from.id)
                    execute(deleteMessage)
                    sendNotificationMessage(message.chatId, "$senderName: $text")
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getSenderName(userId: Long): String {
        val chatMember = execute(GetChatMember(userId.toString(), userId))
        return chatMember.user?.let { "${it.firstName} ${it.lastName?:""}" } ?: ""
    }

    private fun sendNotificationMessage(chatId: Long, text: String) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = text
        message.disableNotification = true
        message.disableWebPagePreview = true
        try {
            execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

}

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    try {
        botsApi.registerBot(MyBot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}