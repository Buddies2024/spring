import logo from '../assets/images/logos/logo.gif';

type Props = {
    isEnd: Boolean;
}

function LogoAnimation(props: Props) {
    const timestamp = Date.now();
    const logoSrc = `${logo}?t=${timestamp}`;
    const className = props.isEnd ? "logo end" : "logo"

    return (
        <img className={className} src={logoSrc} alt="로딩중..." />
    )
}

export default LogoAnimation;
