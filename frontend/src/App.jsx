import { useEffect, useState } from "react";
import api from "./api";

function App() {
  const [payment, setPayment] = useState({
    customerName: "",
    amount: "",
    paymentMethod: "UPI",
    bankName: "",
  });

  const [gateways, setGateways] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [analytics, setAnalytics] = useState({
    totalPayments: 0,
    successfulPayments: 0,
    failedPayments: 0,
    successRate: 0,
    totalRevenue: 0,
  });

  const [message, setMessage] = useState("");

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    await fetchGateways();
    await fetchTransactions();
    await fetchAnalytics();
  };

  const fetchGateways = async () => {
    try {
      const res = await api.get("/api/gateways");
      console.log("Gateways API response:", res.data);

      setGateways(Array.isArray(res.data) ? res.data : []);
    } catch (error) {
      console.error("Error fetching gateways:", error);
      setGateways([]);
    }
  };

  const fetchTransactions = async () => {
    try {
      const res = await api.get("/api/payments");
      console.log("Transactions API response:", res.data);

      setTransactions(Array.isArray(res.data) ? [...res.data].reverse() : []);
    } catch (error) {
      console.error("Error fetching transactions:", error);
      setTransactions([]);
    }
  };

  const fetchAnalytics = async () => {
    try {
      const res = await api.get("/api/payments/analytics");
      console.log("Analytics API response:", res.data);

      setAnalytics(res.data);
    } catch (error) {
      console.error("Error fetching analytics:", error);
    }
  };

  const handleChange = (e) => {
    setPayment({
      ...payment,
      [e.target.name]: e.target.value,
    });
  };

  const makePayment = async (e) => {
    e.preventDefault();

    try {
      const res = await api.post("/api/payments/pay", {
        ...payment,
        amount: Number(payment.amount),
      });

      setMessage(res.data.reason);

      setPayment({
        customerName: "",
        amount: "",
        paymentMethod: "UPI",
        bankName: "",
      });

      loadAllData();
    } catch (error) {
      setMessage("Payment failed. Please check backend connection.");
      console.error(error);
    }
  };

  const updateGatewayStatus = async (id, status) => {
    try {
      await api.put(`/api/gateways/${id}/status?status=${status}`);
      fetchGateways();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="container py-4">
      <div className="text-center mb-4">
        <h1 className="fw-bold">PayRouteX</h1>
        <p className="text-muted">
          Smart Payment Routing Simulator inspired by Juspay-style payment orchestration
        </p>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <div className="card shadow-sm p-3 text-center">
            <h6>Total Payments</h6>
            <h3>{analytics.totalPayments}</h3>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm p-3 text-center">
            <h6>Successful</h6>
            <h3 className="text-success">{analytics.successfulPayments}</h3>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm p-3 text-center">
            <h6>Failed</h6>
            <h3 className="text-danger">{analytics.failedPayments}</h3>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm p-3 text-center">
            <h6>Success Rate</h6>
            <h3>{analytics.successRate.toFixed(2)}%</h3>
          </div>
        </div>
      </div>

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card shadow-sm p-4">
            <h4 className="mb-3">Checkout</h4>

            {message && (
              <div className="alert alert-info">
                {message}
              </div>
            )}

            <form onSubmit={makePayment}>
              <div className="mb-3">
                <label className="form-label">Customer Name</label>
                <input
                  type="text"
                  name="customerName"
                  value={payment.customerName}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Enter customer name"
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Amount</label>
                <input
                  type="number"
                  name="amount"
                  value={payment.amount}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Enter amount"
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Payment Method</label>
                <select
                  name="paymentMethod"
                  value={payment.paymentMethod}
                  onChange={handleChange}
                  className="form-select"
                >
                  <option value="UPI">UPI</option>
                  <option value="CARD">Card</option>
                  <option value="NET_BANKING">Net Banking</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label">Bank Name</label>
                <input
                  type="text"
                  name="bankName"
                  value={payment.bankName}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Example: SBI, HDFC, ICICI"
                  required
                />
              </div>

              <button className="btn btn-primary w-100">
                Pay Now
              </button>
            </form>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="card shadow-sm p-4">
            <h4 className="mb-3">Payment Gateways</h4>

            <div className="table-responsive">
              <table className="table table-bordered align-middle">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>UPI</th>
                    <th>Card</th>
                    <th>Cost</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {gateways.map((gateway) => (
                    <tr key={gateway.id}>
                      <td>{gateway.name}</td>
                      <td>{gateway.upiSuccessRate}%</td>
                      <td>{gateway.cardSuccessRate}%</td>
                      <td>{gateway.costPercentage}%</td>
                      <td>
                        <span
                          className={
                            gateway.status === "ACTIVE"
                              ? "badge bg-success"
                              : "badge bg-danger"
                          }
                        >
                          {gateway.status}
                        </span>
                      </td>
                      <td>
                        {gateway.status === "ACTIVE" ? (
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() =>
                              updateGatewayStatus(gateway.id, "DOWN")
                            }
                          >
                            Mark Down
                          </button>
                        ) : (
                          <button
                            className="btn btn-sm btn-success"
                            onClick={() =>
                              updateGatewayStatus(gateway.id, "ACTIVE")
                            }
                          >
                            Mark Active
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

          </div>
        </div>
      </div>

      <div className="card shadow-sm p-4 mt-4">
        <h4 className="mb-3">Transaction History</h4>

        <div className="table-responsive">
          <table className="table table-striped table-bordered align-middle">
            <thead>
              <tr>
                <th>ID</th>
                <th>Customer</th>
                <th>Amount</th>
                <th>Method</th>
                <th>Selected Gateway</th>
                <th>Fallback</th>
                <th>Status</th>
                <th>Reason</th>
              </tr>
            </thead>

            <tbody>
              {transactions.map((tx) => (
                <tr key={tx.id}>
                  <td>{tx.id}</td>
                  <td>{tx.customerName}</td>
                  <td>₹{tx.amount}</td>
                  <td>{tx.paymentMethod}</td>
                  <td>{tx.selectedGateway}</td>
                  <td>{tx.fallbackGateway || "-"}</td>
                  <td>
                    <span
                      className={
                        tx.status === "SUCCESS"
                          ? "badge bg-success"
                          : "badge bg-danger"
                      }
                    >
                      {tx.status}
                    </span>
                  </td>
                  <td>{tx.reason}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}

export default App;