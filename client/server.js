import express from "express"
import cors from "cors"

const app = express()

app.use(cors())
app.use(express.json())

app.post("/chat",(req,res)=>{

const userMessage = req.body.message

res.json({
reply:"هذا جواب تجريبي على سؤالك: " + userMessage
})

})

app.listen(5000,()=>{
console.log("Server running on port 5000")
})