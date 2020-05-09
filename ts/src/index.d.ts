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
export declare class FirebaseFacade {
    CATEGORY_COLLECTION: string;
    CODE_COLLECTION: string;
    EXAMPLE_COLLECTION: string;
    lastVisibleCodes: {};
    lastVisibleExamples: {};
    signIn(): Promise<User>;
    signOut(): Promise<void>;
    onAuthStateChanged(callback: (user: User) => void): firebase.Unsubscribe;
    fetchCategories(): Promise<Category[]>;
    fetchCodes(categoryId: string): Promise<Code[]>;
    fetchCode(categoryId: string, codeId: string): Promise<Code>;
    saveCode(data: Code): Promise<Code>;
    removeCode(data: Code): Promise<Code>;
    fetchExamples(codeId: string): Promise<Example[]>;
    saveExample(data: Example): Promise<Example>;
}
export {};
