import '../styles/Start.css';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LogoAnimation from "../components/LogoAnimation";
import StartPrompt from "../components/StartPrompt";
import axios from 'axios';
import { setFCMToken } from '../setup-fcm';

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
            requestNotificationPermission();
        } else {
            setEnd(true);
        }
    }

    async function requestNotificationPermission() {
        try {
            const permission = await Notification.requestPermission();
    
            if (permission === 'granted') {
                console.log('알림 권한이 허용되어 있습니다.');
                await setFCMToken();
            } else {
                console.log('알림 권한이 차단되어 있습니다.');
            }
        } catch (err) {
            console.log('알림 권한을 조회하던 도중 에러가 발생했습니다.', err);
        }
    
        startSpring();
    }

    function startSpring() {
        axios.get('/api/anonymous/info')
            .then(response => {
                navigate(getUrl(response.data));
            })
            .catch(error => {
                console.error(error.message);
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
