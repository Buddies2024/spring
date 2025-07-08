import '../styles/Start.css';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LogoAnimation from "../components/LogoAnimation";
import StartPrompt from "../components/StartPrompt";
import axios from 'axios';

type AnonymousInfo = {
    groupId: String,
    shouldLogin: Boolean
}

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
            startSpring();
        } else {
            setEnd(true);
        }
    }

    function startSpring() {
        axios.get('/api/anonymous/info')
            .then((response) => {
                navigate(getUrl(response.data));
            });
    }
    
    function getUrl(anonymousInfo: AnonymousInfo) {
        if (anonymousInfo.shouldLogin) {
            return "/login"
        }
        if (anonymousInfo.groupId === null) {
            return "/groups"
        }
        return `/groups/${anonymousInfo.groupId}`
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
