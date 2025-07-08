import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';

const ReplaceToSpringBoot = () => {
    const location = useLocation();
    const BASE_URL =
        process.env.NODE_ENV === "development"
            ? "http://localhost:8080"
            : "https://myapp.com/api";

    useEffect(() => {
        const path = location.pathname + location.search;
        const springBootUrl = `${BASE_URL}${path}`;
        window.location.replace(springBootUrl);
    }, []);

    return (
        <p>Spring Boot로 리다이렉트 중... ({location.pathname})</p>
    );
}

export default ReplaceToSpringBoot;
