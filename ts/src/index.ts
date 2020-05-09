import * as firebase from "firebase/app";
import "firebase/auth";
import "firebase/firestore";

interface User {
    id: string;
    displayName: string;
    photoUrl: string;
}

interface Category {
    id: string;
    icon: string;
    name: string;
    updatedAt: Date;
}

interface Code {
    id: string;
    categoryId: string;
    userId: string;
    name: string;
    description: string;
    updatedAt: Date;
}

interface Example {
    id: string;
    codeId: string;
    users: User[];
    markdown: string;
    updatedAt: Date;
}

const firebaseConfig = {
    apiKey: process.env.FIREBASE_API_KEY,
    authDomain: process.env.FIREBASE_AUTH_DOMAIN,
    databaseURL: process.env.FIREBASE_DATABASE_URL,
    projectId: process.env.FIREBASE_PROJECT_ID,
    appId: process.env.FIREBASE_APP_ID,
};
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();

export class FirebaseFacade {
    CATEGORY_COLLECTION = "categories";
    CODE_COLLECTION = "codes";
    EXAMPLE_COLLECTION = "examples";
    lastVisibleCodes = {};
    lastVisibleExamples = {};

    async signIn(): Promise<User> {
        const provider = new firebase.auth.GoogleAuthProvider();
        const user = await firebase.auth().signInWithPopup(provider);
        return {
            id: user.user.uid,
            displayName: user.user.displayName,
            photoUrl: user.user.photoURL,
        };
    }

    async signOut() {
        await firebase.auth().signOut();
    }

    onAuthStateChanged(callback: (user: User) => void): firebase.Unsubscribe {
        return firebase.auth().onAuthStateChanged((user) => {
            if (user) {
                callback({
                    id: user.uid,
                    displayName: user.displayName,
                    photoUrl: user.photoURL,
                });
            }
        });
    }

    async fetchCategories(): Promise<Category[]> {
        const query = await db
            .collection(this.CATEGORY_COLLECTION)
            .orderBy("name")
            .get();
        return query.docs.map((d) => {
            const data = d.data();
            return {
                id: d.id.toLowerCase(),
                icon: data["icon"],
                name: data["name"],
                updatedAt: data["updatedAt"],
            };
        });
    }

    async fetchCodes(categoryId: string): Promise<Code[]> {
        if (!this.lastVisibleCodes[categoryId]) {
            this.lastVisibleCodes = {};
            this.lastVisibleExamples = {};
        }

        const query = this.lastVisibleCodes[categoryId]
            ? await db
                  .collection(this.CATEGORY_COLLECTION)
                  .doc(categoryId.toLowerCase())
                  .collection(this.CODE_COLLECTION)
                  .orderBy("updatedAt", "desc")
                  .startAt(this.lastVisibleCodes[categoryId])
                  .limit(15)
                  .get()
            : await db
                  .collection(this.CATEGORY_COLLECTION)
                  .doc(categoryId.toLowerCase())
                  .collection(this.CODE_COLLECTION)
                  .orderBy("updatedAt", "desc")
                  .limit(15)
                  .get();
        const docs = query.docs;
        this.lastVisibleCodes[categoryId] = docs[docs.length - 1];

        return docs.map((d) => {
            const data = d.data();
            return {
                id: d.id.toLowerCase(),
                categoryId: categoryId.toLowerCase(),
                userId: data["userId"],
                name: data["name"],
                description: data["description"],
                updatedAt: data["updatedAt"],
            };
        });
    }

    async fetchCode(categoryId: string, codeId: string): Promise<Code> {
        const query = await db
            .collection(this.CATEGORY_COLLECTION)
            .doc(categoryId.toLowerCase())
            .collection(this.CODE_COLLECTION)
            .doc(codeId.toLowerCase())
            .get();
        const doc = query.data();

        return {
            id: codeId.toLowerCase(),
            categoryId: categoryId.toLowerCase(),
            userId: doc["userId"],
            name: doc["name"],
            description: doc["description"],
            updatedAt: doc["updatedAt"],
        };
    }

    async saveCode(data: Code) {
        await db
            .collection(this.CATEGORY_COLLECTION)
            .doc(data.categoryId)
            .collection(this.CODE_COLLECTION)
            .doc(data.id)
            .set(data);
        return data;
    }

    async removeCode(data: Code) {
        await db
            .collection(this.CATEGORY_COLLECTION)
            .doc(data.categoryId)
            .collection(this.CODE_COLLECTION)
            .doc(data.id)
            .delete();
        return data;
    }

    async fetchExamples(codeId: string): Promise<Example[]> {
        if (!this.lastVisibleExamples[codeId]) {
            this.lastVisibleExamples = {};
        }
        const query = this.lastVisibleExamples[codeId]
            ? await db
                  .collection(this.CODE_COLLECTION)
                  .doc(codeId.toLowerCase())
                  .collection(this.EXAMPLE_COLLECTION)
                  .orderBy("updatedAt", "desc")
                  .startAt(this.lastVisibleExamples[codeId])
                  .limit(5)
                  .get()
            : await db
                  .collection(this.CODE_COLLECTION)
                  .doc(codeId.toLowerCase())
                  .collection(this.EXAMPLE_COLLECTION)
                  .orderBy("updatedAt", "desc")
                  .limit(5)
                  .get();

        const docs = query.docs;
        this.lastVisibleExamples[codeId] = docs[docs.length - 1];

        return docs.map((d) => {
            const data = d.data();
            return {
                id: d.id.toLowerCase(),
                codeId: codeId.toLowerCase(),
                users: data["users"],
                markdown: data["markdown"],
                updatedAt: data["updatedAt"],
            };
        });
    }

    async saveExample(data: Example) {
        await db
            .collection(this.CODE_COLLECTION)
            .doc(data.codeId)
            .collection(this.EXAMPLE_COLLECTION)
            .doc(data.id)
            .set(data);
        return data;
    }
}
