import React, { useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const initialBooks = [
  { id: 1, isbn: '9780134685991', title: 'Effective Java', author: 'Joshua Bloch', category: 'Programming', status: 'Available' },
  { id: 2, isbn: '9780132350884', title: 'Clean Code', author: 'Robert C. Martin', category: 'Programming', status: 'Issued' },
  { id: 3, isbn: '9780262046305', title: 'Introduction to Algorithms', author: 'Thomas H. Cormen', category: 'Computer Science', status: 'Available' },
  { id: 4, isbn: '9781491950357', title: 'Designing Data-Intensive Applications', author: 'Martin Kleppmann', category: 'Architecture', status: 'Available' }
];

const initialMembers = [
  { id: 'M001', name: 'Aarav Sharma', email: 'aarav@example.com', activeLoans: 1 },
  { id: 'M002', name: 'Priya Singh', email: 'priya@example.com', activeLoans: 0 },
  { id: 'M003', name: 'Rahul Verma', email: 'rahul@example.com', activeLoans: 0 }
];

function App() {
  const [books, setBooks] = useState(() => JSON.parse(localStorage.getItem('lms-books') || 'null') || initialBooks);
  const [members, setMembers] = useState(() => JSON.parse(localStorage.getItem('lms-members') || 'null') || initialMembers);
  const [query, setQuery] = useState('');
  const [tab, setTab] = useState('Dashboard');
  const [toast, setToast] = useState('');

  const persist = (nextBooks, nextMembers) => {
    setBooks(nextBooks); setMembers(nextMembers);
    localStorage.setItem('lms-books', JSON.stringify(nextBooks));
    localStorage.setItem('lms-members', JSON.stringify(nextMembers));
  };

  const notify = (message) => { setToast(message); setTimeout(() => setToast(''), 2200); };

  const issueBook = (bookId) => {
    const book = books.find(b => b.id === bookId);
    if (!book || book.status === 'Issued') return notify('Book is already issued.');
    const member = members.find(m => m.activeLoans === 0);
    if (!member) return notify('No member is currently eligible for a loan.');
    const nextBooks = books.map(b => b.id === bookId ? { ...b, status: 'Issued' } : b);
    const nextMembers = members.map(m => m.id === member.id ? { ...m, activeLoans: m.activeLoans + 1 } : m);
    persist(nextBooks, nextMembers); notify(`${book.title} issued to ${member.name}.`);
  };

  const returnBook = (bookId) => {
    const book = books.find(b => b.id === bookId);
    if (!book || book.status !== 'Issued') return notify('Book is already available.');
    const member = members.find(m => m.activeLoans > 0);
    const nextBooks = books.map(b => b.id === bookId ? { ...b, status: 'Available' } : b);
    const nextMembers = member ? members.map(m => m.id === member.id ? { ...m, activeLoans: Math.max(0, m.activeLoans - 1) } : m) : members;
    persist(nextBooks, nextMembers); notify(`${book.title} returned successfully.`);
  };

  const addBook = () => {
    const title = window.prompt('Book title');
    if (!title?.trim()) return;
    const author = window.prompt('Author') || 'Unknown Author';
    const isbn = window.prompt('ISBN') || `TEMP-${Date.now()}`;
    if (books.some(b => b.isbn === isbn)) return notify('ISBN already exists.');
    const next = [...books, { id: Date.now(), isbn, title: title.trim(), author, category: 'General', status: 'Available' }];
    persist(next, members); notify('Book added.');
  };

  const filteredBooks = useMemo(() => books.filter(b => `${b.title} ${b.author} ${b.isbn} ${b.category}`.toLowerCase().includes(query.toLowerCase())), [books, query]);
  const issued = books.filter(b => b.status === 'Issued').length;
  const available = books.length - issued;

  return <div className="app">
    <aside className="sidebar">
      <div className="brand"><div className="logo">L</div><div><strong>Libra</strong><span>Library Manager</span></div></div>
      <nav>{['Dashboard', 'Books', 'Members'].map(item => <button className={tab === item ? 'active' : ''} onClick={() => setTab(item)} key={item}>{item === 'Dashboard' ? '⌂' : item === 'Books' ? '▣' : '♙'}<span>{item}</span></button>)}</nav>
      <div className="sidebar-note"><b>Java OOP Project</b><p>Web interface for the Library Management System.</p></div>
    </aside>

    <main>
      <header><div><p className="eyebrow">LIBRARY MANAGEMENT</p><h1>{tab}</h1></div><button className="primary" onClick={addBook}>+ Add Book</button></header>

      {tab === 'Dashboard' && <>
        <section className="stats">
          <article><span>Total Books</span><strong>{books.length}</strong><small>Catalog inventory</small></article>
          <article><span>Available</span><strong>{available}</strong><small>Ready to issue</small></article>
          <article><span>Issued</span><strong>{issued}</strong><small>Active checkouts</small></article>
          <article><span>Members</span><strong>{members.length}</strong><small>Registered users</small></article>
        </section>
        <section className="panel"><div className="panel-head"><div><h2>Book Inventory</h2><p>Track catalog availability and checkout status.</p></div><input placeholder="Search books..." value={query} onChange={e => setQuery(e.target.value)} /></div><BookTable books={filteredBooks} issueBook={issueBook} returnBook={returnBook}/></section>
      </>}

      {tab === 'Books' && <section className="panel"><div className="panel-head"><div><h2>Book Catalog</h2><p>Manage every book in the library.</p></div><input placeholder="Search by title, author, ISBN..." value={query} onChange={e => setQuery(e.target.value)} /></div><BookTable books={filteredBooks} issueBook={issueBook} returnBook={returnBook}/></section>}

      {tab === 'Members' && <section className="panel"><div className="panel-head"><div><h2>Members</h2><p>Registered library users and active loans.</p></div></div><div className="member-grid">{members.map(m => <article className="member" key={m.id}><div className="avatar">{m.name.split(' ').map(x => x[0]).join('')}</div><div><h3>{m.name}</h3><p>{m.email}</p><span>{m.id} · {m.activeLoans} active loan{m.activeLoans === 1 ? '' : 's'}</span></div></article>)}</div></section>}
      {toast && <div className="toast">✓ {toast}</div>}
    </main>
  </div>;
}

function BookTable({ books, issueBook, returnBook }) {
  return <div className="table-wrap"><table><thead><tr><th>Book</th><th>ISBN</th><th>Category</th><th>Status</th><th>Action</th></tr></thead><tbody>{books.map(b => <tr key={b.id}><td><b>{b.title}</b><small>{b.author}</small></td><td>{b.isbn}</td><td>{b.category}</td><td><span className={`badge ${b.status.toLowerCase()}`}>{b.status}</span></td><td><button className="action" onClick={() => b.status === 'Available' ? issueBook(b.id) : returnBook(b.id)}>{b.status === 'Available' ? 'Issue' : 'Return'}</button></td></tr>)}{books.length === 0 && <tr><td colSpan="5" className="empty">No books found.</td></tr>}</tbody></table></div>;
}

createRoot(document.getElementById('root')).render(<App />);
