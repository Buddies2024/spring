import React, { useEffect, useState } from "react";
import '../styles/Start.css';
import LogoAnimation from "../components/LogoAnimation.tsx";
import StartPrompt from "../components/StartPrompt.tsx";

const Start = () => {
    const [isEnd, setEnd] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => {
            setEnd(true);
        }, 2400);

        return () => {
            clearTimeout(timer);
        }
    }, []);

    const handleClick = () => {
        if (isEnd) {
            alert("애니메이션 끝남");
        } else {
            setEnd(true);
        }
    }

    return (
        <div className="background start-page" onClick={handleClick}>
            <LogoAnimation isEnd={isEnd} />
            <div className="title-box">
                <span className="text spring" >스프링</span>
            </div>
            <div className="bottom">
                <StartPrompt isEnd={isEnd} />
            </div>
        </div>
    )
}

export default Start;
