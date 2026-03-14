import { useRef } from "react"
import Header from "./components/Header/Header"
import HeroSection from "./components/Hero/HeroSection"
import Chat from "./components/Chat/ChatBox"

function App() {

  const chatRef = useRef()

  const handleAsk = (question) => {
    chatRef.current.sendMessage(question)
  }

  return (
    <>
      <Header />

      <HeroSection onAsk={handleAsk} />

      <Chat ref={chatRef} />
    </>
  )

}

export default App