import "./Header.css";

function Header() {

    return (

        <header className="header">

            {/* LOGO RIGHT */}

            <div className="logo">
                <div className="logo-icon">
                    ⚖️
                </div>
                <h2 className="logo-text">
                    <span className="gold">حق</span>
                    <span className="green">ي</span>
                </h2>



            </div>


            {/* NAVIGATION */}

            <nav className="nav">

                <a href="#">الرئيسية</a>

                <a href="#" className="ai-link">
                    🤖 المساعد AI
                </a>

                <a href="#">العقود 📄</a>
                <a href="#">المحامون ⚖️</a>
                <a href="#">ثقافة قانونية 🎓</a>
                <a href="#">لوحتي 📊</a>
                <a href="#">Business Model 💼</a>

                <span className="bell">
                    🔔
                    <span className="notif-dot"></span>
                </span>

            </nav>


            {/* LEFT BUTTONS */}

            <div className="actions">

                <button className="dashboard">
                    لوحتي
                </button>

                <button className="start">
                    ابدأ مجاناً
                </button>

            </div>

        </header>

    )

}

export default Header