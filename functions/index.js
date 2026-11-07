const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// 1. Chat Message Push Notification Trigger
exports.sendChatNotification = functions.firestore
    .document("chatRooms/{chatRoomId}/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.data();
        const receiverId = messageData.receiverId;
        const senderId = messageData.senderId;
        const text = messageData.messageType === "IMAGE" ? "📷 Shared a photo" : messageData.text;

        try {
            // Get sender name
            const senderDoc = await admin.firestore().collection("users").doc(senderId).get();
            const senderName = senderDoc.exists ? senderDoc.data().name : "New Message";

            // Get receiver fcmToken
            const receiverDoc = await admin.firestore().collection("users").doc(receiverId).get();
            if (!receiverDoc.exists) return null;

            const fcmToken = receiverDoc.data().fcmToken;
            if (!fcmToken) return null;

            // Construct FCM Payload
            const payload = {
                token: fcmToken,
                notification: {
                    title: senderName,
                    body: text,
                },
                data: {
                    click_action: "FLUTTER_NOTIFICATION_CLICK",
                    sound: "default",
                },
            };

            // Send notification via FCM
            await admin.messaging().send(payload);
            console.log("Chat notification sent successfully!");
        } catch (error) {
            console.error("Error sending chat notification:", error);
        }
        return null;
    });

// 2. Incoming Call Push Notification Trigger
exports.sendCallNotification = functions.firestore
    .document("calls/{userId}")
    .onCreate(async (snapshot, context) => {
        const callData = snapshot.data();
        const receiverId = context.params.userId;
        const callerName = callData.callerName || "Incoming Call";

        try {
            const receiverDoc = await admin.firestore().collection("users").doc(receiverId).get();
            if (!receiverDoc.exists) return null;

            const fcmToken = receiverDoc.data().fcmToken;
            if (!fcmToken) return null;

            const payload = {
                token: fcmToken,
                notification: {
                    title: "🚨 Incoming Call",
                    body: `${callerName} is calling you...`,
                },
                data: {
                    click_action: "FLUTTER_NOTIFICATION_CLICK",
                    sound: "default",
                },
            };

            await admin.messaging().send(payload);
            console.log("Call notification sent successfully!");
        } catch (error) {
            console.error("Error sending call notification:", error);
        }
        return null;
    });
