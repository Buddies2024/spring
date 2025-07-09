import './styles/App.css';
import { useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { registerServiceWorker, handleMessage } from './setup-fcm';
// import NotFound from "./pages/NotFound";
import Login from "./pages/Login";
import Start from "./pages/Start";
import ReplaceToSpringBoot from "./pages/ReplaceToSpringBoot";

function App() {
  useEffect(() => {
    registerServiceWorker();
    handleMessage();
  }, []);

  return (
    <BrowserRouter>
      <div className="App">
        <Routes>
          <Route path="/react" element={<Start />}></Route>
          <Route path="/react/login" element={<Login />}></Route>

          <Route path="*" element={<ReplaceToSpringBoot />} />
          {/* <Route path="*" element={<NotFound />}></Route> */}
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
