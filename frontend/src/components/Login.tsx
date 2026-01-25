import { getRedirectResult, signInWithEmailAndPassword, signInWithRedirect } from "firebase/auth";
import { useEffect, useState } from "react"
import { auth, googleProvider } from "../firebase";


export default function Login() {
  const[email,setEmail] = useState('');
  const [ password, setPassword] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
   (async () => {
    try {
      await getRedirectResult(auth);
    } catch (error: any) {
      console.error(error)
      setError(error.code ?? error.message);
    }
   }) ();
  },[])

  const handleLogin = async(e:React.FormEvent) => {
    e.preventDefault();
    try{
      await signInWithEmailAndPassword(auth, email, password);
    }
    catch(err: any) {
      setError(err.message);
    }
  };

  const handleLoginGoogle = async() => {
    try {
      await signInWithRedirect(auth, googleProvider);
    } catch (error: any) {
      setError(error.message ?? error.code)
      console.error(error)
    }
  }

  return (
    <>
    <form onSubmit={handleLogin}>
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button type="submit">Login</button>
      {error && <p>{error}</p>}

    </form>
    <button onClick={handleLoginGoogle}>

    </button>

    </>
  );
}
