import React, { Component } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import NotFound from "./pages/NotFound.tsx";
import Login from "./pages/Login.tsx";
import Start from "./pages/Start.tsx";
import './styles/App.css';

class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <div className="App">
          <Routes>
            <Route path="/react" element={<Start />}></Route>
            <Route path="/react/login" element={<Login />}></Route>
            <Route path="*" element={<NotFound />}></Route>
          </Routes>
        </div>
      </BrowserRouter>
    );
  }
}

export default App;
