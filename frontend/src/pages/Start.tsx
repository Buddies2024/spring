import '../styles/Start.css';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LogoAnimation from "../components/LogoAnimation";
import StartPrompt from "../components/StartPrompt";
import axios from 'axios';

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
            startSpring()
            // navigate("/login");
        } else {
            setEnd(true);
        }
    }

    function startSpring() {
        axios.get('/api/anonymous/info')
            .then((response) => {
                console.log(response.data);
            })
    }
    
    function getUrl(anonymousInfo: any) {
        window.localStorage.removeItem("groupId");
    
        if (anonymousInfo.shouldLogin) {
            return "/login"
        }
    
        if (anonymousInfo.groupId === null) {
            return "/groups"
        }
    
        window.localStorage.setItem("groupId", anonymousInfo.groupId);
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
