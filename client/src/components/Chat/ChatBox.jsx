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

    <div className="wrapper">

      <div className="chatArea">

        {messages.map((msg, i) =>
          msg.type === "user" ? (

            <div key={i} className="userRow">

              <div className="userBubble">
                {msg.text}
                <span className="msgTime">{formatTime(msg.time)}</span>
              </div>

              <div className="userAvatar">
                ر
              </div>

            </div>

          ) : (

            <div key={i} className="aiRow">

              <div className="aiAvatar">
                ⚖️
              </div>

              <div className="aiBubble">
                {msg.text}
                <span className="msgTime">{formatTime(msg.time)}</span>
              </div>

            </div>

          )
        )}

        {loading && (

          <div className="aiRow">

            <div className="aiAvatar">
              ⚖️
            </div>

            <div className="aiBubble typingBubble">
              <span className="dot" />
              <span className="dot" />
              <span className="dot" />
            </div>

          </div>

        )}

        <div ref={messagesEndRef} />

      </div>

      <div className="inputWrapper">

        <div className="suggestions">

          {SUGGESTIONS.map((s, i) => (

            <button
              key={i}
              className="chip"
              onClick={() => sendMessage(s)}
            >
              {s}
            </button>

          ))}

        </div>

        <div className="inputRow">

          <button
            className="sendBtn"
            onClick={() => sendMessage()}
          >
            →
          </button>

          <input
            className="input"
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

  )

})

export default ChatBox