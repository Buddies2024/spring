importScripts("https://www.gstatic.com/firebasejs/10.11.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.11.0/firebase-messaging-compat.js");

const firebaseConfig = {
  apiKey: "AIzaSyAqVv93bD1Po8OHgZhZXfaPBOImUN8ZTNg",
  authDomain: "buddies-spring.firebaseapp.com",
  projectId: "buddies-spring",
  storageBucket: "buddies-spring.firebasestorage.app",
  messagingSenderId: "271822946508",
  appId: "1:271822946508:web:3cf3224415f64c2e8b8269",
  measurementId: "G-QSJPG39DJD"
}

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
    console.log(
      "백그라운드 알림 도착",
      payload
    );
  
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
      body: payload.notification.body,
      icon: payload.notification.icon,
    };
  
    return self.registration.showNotification(
      notificationTitle,
      notificationOptions
    );
  });
