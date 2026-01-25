import { getRedirectResult, onAuthStateChanged, signOut, type User } from "firebase/auth"
import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "../firebase";


type AuthContextType = {
  user: User | null;
  loading: boolean;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: React.ReactNode}) {

  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
  (async () => {
    try {
      const res = await getRedirectResult(auth);
      console.log("redirect result:", res?.user?.email ?? null);
    } catch (e: any) {
      console.error("redirect error:", e?.code, e?.message);
    }
  })();

  const unsubscribe = onAuthStateChanged(auth, (user) => {
    setUser(user);
    setLoading(false);
  });

  return unsubscribe;
}, []);

  const logout = async () => {
    await signOut(auth);
  };

  return(
    <AuthContext.Provider value={ {user, loading, logout}}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if(!context) throw new Error('useAuth muse be used within AuthProvider')
    return context;
}