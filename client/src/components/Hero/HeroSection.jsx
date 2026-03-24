import "./HeroSection.css"
import categories from "../../data/legalCategories"
import robot from "../../assets/robot.png"

function HeroSection({ onAsk }) {

    return (

        <section className="hero">

            <div className="hero-container">

                <div className="hero-text">

                    <h1>
                        المساعد القانوني الذكي 🤖
                    </h1>

                    <p>
                        اسأل بالدارجة أو العربية — مدرب على القانون المغربي
                    </p>

                    <div className="categories">

                        {categories.map((cat, index) => (
                            <button
                                key={index}
                                onClick={() => onAsk(cat.question)}
                            >
                                {cat.icon} {cat.name}
                            </button>
                        ))}

                    </div>

                </div>

                <div className="hero-image">

                    <img src={robot} alt="robot" />

                </div>

            </div>

        </section>

    )

}

export default HeroSection