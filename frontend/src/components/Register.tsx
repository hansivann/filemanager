import { createUserWithEmailAndPassword } from "firebase/auth";
import { useState } from "react"
import { auth } from "../firebase";

export function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleRegister = async(e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createUserWithEmailAndPassword(auth, email, password);
    } catch (error: any) {
      setError(error.message)
    }
  };

  return(
    <form onSubmit={handleRegister}>
      <input
      type="email"
      placeholder="Email"
      value = {email}
      onChange={(e) => setEmail(e.target.value)}
      />

      <input
      type="password"
      placeholder="Password"
      value={password}
      onChange={(e) => { setPassword(e.target.value)
      }}
      />

      <button type="submit">Register</button>
      {error && <p>Error</p>}

    </form>
  )

}