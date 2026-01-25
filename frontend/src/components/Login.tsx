import { signInWithEmailAndPassword, signInWithPopup } from "firebase/auth";
import { useState } from "react"
import { auth, googleProvider } from "../firebase";


export default function Login() {
  const[email,setEmail] = useState('');
  const [ password, setPassword] = useState('');
  const [error, setError] = useState('');

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
      await signInWithPopup(auth, googleProvider);
    } catch (error: any) {
      setError(error.message)
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
    <div>
      <button onClick={handleLoginGoogle} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
  <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth_providers/google.svg" alt="Google" width="20" />
  Sign in with Google
</button>
    </div>
    </>
  );
}
