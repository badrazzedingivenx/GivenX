import { useState, useRef, useEffect, forwardRef, useImperativeHandle } from "react"
import "./ChatBox.css"

const SUGGESTIONS = ["فسخ الكراء", "مدة الإشعار", "تسجيل شركة", "الطرد التعسفي"]

const ChatBox = forwardRef((props, ref) => {

  const [messages, setMessages] = useState([])
  const [input, setInput] = useState("")
  const [loading, setLoading] = useState(false)

  const messagesEndRef = useRef(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [messages])

  const formatTime = (date) => {
    if (!date) return ""
    return date.toLocaleTimeString("ar-MA", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    })
  }

  const sendMessage = async (textFromButton) => {

    const message = textFromButton || input
    if (message.trim() === "") return

    setMessages(prev => [...prev, { text: message, type: "user", time: new Date() }])
    setInput("")

    await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)))

    setLoading(true)

    try {

      const response = await fetch("http://localhost:5000/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ message })
      })

      const data = await response.json()

      setMessages(prev => [...prev, { text: data.reply, type: "ai", time: new Date() }])

    } catch {

      setMessages(prev => [
        ...prev,
        { text: "حدث خطأ في الاتصال بالخادم", type: "ai", time: new Date() }
      ])

    } finally {

      setLoading(false)

    }

  }

  useImperativeHandle(ref, () => ({
    sendMessage
  }))

  return (

    <div className="chat-container">

      <div className="chat-messages">

        {messages.map((msg, i) =>
          msg.type === "user" ? (

            <div key={i} className="message-wrapper user">
              <div className="message-bubble">
                {msg.text}
              </div>
              <div className="user-avatar">ر</div>
            </div>

          ) : (

            <div key={i} className="message-wrapper ai">
              <div className="ai-avatar-circle">
                <span className="ai-icon">⚖️</span>
              </div>
              <div className="message-bubble">
                <div className="ai-intro">
                  السلام عليكم! أنا المساعد القانوني لـ <b>حقي</b> 🇲🇦
                </div>
                <div className="ai-body">
                  {msg.text}
                </div>
                <span className="msg-time">{formatTime(msg.time)}</span>
              </div>
            </div>

          )
        )}

        {/* Initial message if empty to match image */}
        {messages.length === 0 && (
          <div className="message-wrapper ai">
            <div className="ai-avatar-circle">
              <span className="ai-icon">⚖️</span>
            </div>
            <div className="message-bubble">
              <div className="ai-intro">
                السلام عليكم! أنا المساعد القانوني لـ <span className="brand-name">حقي</span> 🇲🇦
              </div>
              <div className="ai-body">
                يمكنني مساعدتك في القانون المغربي — كراء، شغل، أسرة، تجارة...
                <br />
                اسأل بالدارجة أو العربية!
              </div>
              <span className="msg-time">الآن</span>
            </div>
          </div>
        )}

        {loading && (
          <div className="message-wrapper ai">
            <div className="ai-avatar-circle">
              <span className="ai-icon">⚖️</span>
            </div>
            <div className="message-bubble typing">
              <span className="dot" />
              <span className="dot" />
              <span className="dot" />
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input-section">
        <div className="input-outer-box">
          <div className="suggestions-bar">
            {SUGGESTIONS.map((s, i) => (
              <button
                key={i}
                className="suggestion-chip"
                onClick={() => sendMessage(s)}
              >
                {s}
              </button>
            ))}
          </div>

          <div className="input-inner-box">
            <button
              className="send-button"
              onClick={() => sendMessage()}
            >
              <span className="send-arrow">←</span>
            </button>
            <input
              className="chat-input"
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && sendMessage()}
              placeholder="اكتب سؤالك القانوني..."
              dir="rtl"
            />
          </div>
        </div>
      </div>

    </div>

  )

})

export default ChatBox