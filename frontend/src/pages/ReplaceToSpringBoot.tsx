import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';

const ReplaceToSpringBoot = () => {
    const location = useLocation();
    const hostname = window.location.hostname;
    const BASE_URL =
        process.env.NODE_ENV === "development"
            ? `http://${hostname}:8080`
            : "https://buddies-spring.site";

    useEffect(() => {
        const path = location.pathname + location.search;
        const springBootUrl = `${BASE_URL}${path}`;
        window.location.replace(springBootUrl);
    }, [location, BASE_URL]);

    return (
        <p>Spring Boot로 리다이렉트 중... ({location.pathname})</p>
    );
}

export default ReplaceToSpringBoot;
