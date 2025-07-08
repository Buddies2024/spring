import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LogoAnimation from "../components/LogoAnimation.tsx";
import StartPrompt from "../components/StartPrompt.tsx";
import '../styles/Start.css';

const Start = () => {
    const [isEnd, setEnd] = useState(false);
    const navigate = useNavigate();

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
            navigate("/react/login");
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
