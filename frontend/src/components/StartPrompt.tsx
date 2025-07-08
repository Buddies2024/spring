import { useEffect, useState } from "react";

type Props = {
    isEnd: Boolean;
}

function StartPrompt(props: Props) {
    const [animation, setAnimation] = useState("");

    useEffect(() => {
        let timer: NodeJS.Timeout;

        if (props.isEnd) {
            setAnimation('typing');

            timer = setTimeout(() => {
                setAnimation('blinking-text');
            }, 1000);
        }

        return () => {
            clearTimeout(timer);
        }
    }, [props.isEnd]);

    return (
        <span className={`start-prompt ${animation}`}>화면을 눌러 시작하기</span>
    )
}

export default StartPrompt;
