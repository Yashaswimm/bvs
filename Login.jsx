import { useContext, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import toast from "react-hot-toast";
import Card from "../components/Card";
import Button from "../components/Button";
import { RoleContext } from "../contexts/RoleContext";

export default function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const { updateRole } = useContext(RoleContext);

  /* const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8085/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });
      
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('token', token);
        localStorage.setItem('email', formData.email);
       // sessionStorage.setItem("roles",data.roles);
        toast.success('Login successful!');
        navigate('/passenger/dashboard');
      } else {
        toast.error('Invalid credentials');
      }
    } catch (error) {
      toast.error('Login failed');
    }
  };  */

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8085/api/auth/login", {
        method: "POST",

        headers: { "Content-Type": "application/json" },

        body: JSON.stringify(formData),
      });

      const data = await response.json();
      console.log(data);
      if (response.ok) {
        // Store the token, role, and username in session storage

        sessionStorage.setItem("token", data.token);

        sessionStorage.setItem("role", data.roles);
        toast.success("Login successful!");

        //sessionStorage.setItem("id", data.id);

        //sessionStorage.setItem("username", form.username);

        // Redirect based on role 
        navigate(`/profile`)

        updateRole(data.roles);

        if (data.roles == "PASSENGER") {
          navigate("/passenger/dashboard");
        } 
        if(data.roles=="ADMIN")
        {
          navigate("/admin/dashboard");
        }
      } else {
        console.log("Login failed");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <Card>
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-blue-900 mb-2">Welcome Back!</h2>
        <p className="text-gray-600">Sign in to manage your bus services</p>
      </div>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            type="email"
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-300"
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
            required
            placeholder="Enter your email"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            type="password"
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-300"
            value={formData.password}
            onChange={(e) =>
              setFormData({ ...formData, password: e.target.value })
            }
            required
            placeholder="Enter your password"
          />
        </div>
        <Button type="submit" fullWidth>
          Login
        </Button>
        <div className="text-center mt-6">
          <p className="text-gray-600">
            Don't have an account?{" "}
            <Link
              to="/register"
              className="text-blue-600 hover:text-blue-800 font-medium"
            >
              Register here
            </Link>
          </p>
        </div>
      </form>
    </Card>
  );
}
