import React, { useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const seedBooks = [
  { id: 1, isbn: '9780134685991', title: 'Effective Java', author: 'Joshua Bloch', category: 'Programming', status: 'Available', issuedTo: null },
  { id: 2, isbn: '9780132350884', title: 'Clean Code', author: 'Robert C. Martin', category: 'Programming', status: 'Issued', issuedTo: 'M001' },
  { id: 3, isbn: '9780262046305', title: 'Introduction to Algorithms', author: 'Thomas H. Cormen', category: 'Computer Science', status: 'Available', issuedTo: null },
  { id: 4, isbn: '9781491950357', title: 'Designing Data-Intensive Applications', author: 'Martin Kleppmann', category: 'Architecture', status: 'Available', issuedTo: null }
];
const seedMembers = [
  { id: 'M001', name: 'Aarav Sharma', email: 'aarav@example.com', activeLoans: 1 },
  { id: 'M002', name: 'Priya Singh', email: 'priya@example.com', activeLoans: 0 },
  { id: 'M003', name: 'Rahul Verma', email: 'rahul@example.com', activeLoans: 0 }
];
function load(key, fallback) { try { const value = localStorage.getItem(key); return value ? JSON.parse(value) : fallback; } catch { return fallback; } }
function App() {
  const [books, setBooks] = useState(() => load('lms-books', seedBooks));
  const [members, setMembers] = useState(() => load('lms-members', seedMembers));
  const [tab, setTab] = useState('Dashboard'), [query, setQuery] = useState(''), [statusFilter, setStatusFilter] = useState('All');
  const [selectedMember, setSelectedMember] = useState(''), [toast, setToast] = useState('');
  const [showBook, setShowBook] = useState(false), [showMember, setShowMember] = useState(false);
  const [bookForm, setBookForm] = useState({ title: '', author: '', isbn: '', category: 'General' });
  const [memberForm, setMemberForm] = useState({ name: '', email: '' });
  const notify = (message) => { setToast(message); clearTimeout(window.__lmsToast); window.__lmsToast = setTimeout(() => setToast(''), 2600); };
  const persist = (nextBooks, nextMembers) => { setBooks(nextBooks); setMembers(nextMembers); localStorage.setItem('lms-books', JSON.stringify(nextBooks)); localStorage.setItem('lms-members', JSON.stringify(nextMembers)); };
  const issueBook = (bookId) => {
    if (!selectedMember) return notify('Select a member before issuing a book.');
    const book = books.find(b => b.id === bookId), member = members.find(m => m.id === selectedMember);
    if (!book || book.status !== 'Available' || !member) return notify('This book is not available.');
    persist(books.map(b => b.id === bookId ? { ...b, status: 'Issued', issuedTo: member.id } : b), members.map(m => m.id === member.id ? { ...m, activeLoans: m.activeLoans + 1 } : m));
    setSelectedMember(''); notify(`${book.title} issued to ${member.name}.`);
  };
  const returnBook = (bookId) => {
    const book = books.find(b => b.id === bookId); if (!book || book.status !== 'Issued') return notify('Book is already available.');
    const borrower = members.find(m => m.id === book.issuedTo);
    persist(books.map(b => b.id === bookId ? { ...b, status: 'Available', issuedTo: null } : b), borrower ? members.map(m => m.id === borrower.id ? { ...m, activeLoans: Math.max(0, m.activeLoans - 1) } : m) : members);
    notify(`${book.title} returned successfully.`);
  };
  const addBook = (e) => { e.preventDefault(); const title = bookForm.title.trim(), author = bookForm.author.trim(), isbn = bookForm.isbn.trim(); if (!title || !author || !isbn) return notify('Title, author and ISBN are required.'); if (books.some(b => b.isbn === isbn)) return notify('ISBN already exists.'); persist([...books, { id: Date.now(), ...bookForm, title, author, isbn, status: 'Available', issuedTo: null }], members); setBookForm({ title: '', author: '', isbn: '', category: 'General' }); setShowBook(false); notify('Book added to the catalog.'); };
  const addMember = (e) => { e.preventDefault(); const name = memberForm.name.trim(), email = memberForm.email.trim(); if (!name || !email) return notify('Name and email are required.'); persist(books, [...members, { id: `M${String(Date.now()).slice(-4)}`, name, email, activeLoans: 0 }]); setMemberForm({ name: '', email: '' }); setShowMember(false); notify('Member added successfully.'); };
  const filteredBooks = useMemo(() => books.filter(b => `${b.title} ${b.author} ${b.isbn} ${b.category}`.toLowerCase().includes(query.toLowerCase().trim()) && (statusFilter === 'All' || b.status === statusFilter)), [books, query, statusFilter]);
  const issued = books.filter(b => b.status === 'Issued').length, available = books.length - issued, loans = members.reduce((s, m) => s + m.activeLoans, 0);
  return <div className="app"><aside className="sidebar"><div className="brand"><div className="logo">L</div><div><strong>Libra</strong><span>Library Manager</span></div></div><nav>{['Dashboard','Books','Members'].map(item => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item === 'Dashboard' ? '⌂' : item === 'Books' ? '▣' : '♙'}<span>{item}</span></button>)}</nav><div className="sidebar-note"><b>Java 17 + React</b><p>OOP domain model with a responsive browser dashboard.</p></div><button className="reset" onClick={() => { persist(seedBooks, seedMembers); notify('Demo data restored.'); }}>Reset demo data</button></aside>
  <main><header><div><p className="eyebrow">LIBRARY MANAGEMENT</p><h1>{tab}</h1><p className="subtitle">Manage books, members and active loans.</p></div>{tab === 'Members' ? <button className="primary" onClick={() => setShowMember(true)}>+ Add Member</button> : <button className="primary" onClick={() => setShowBook(true)}>+ Add Book</button>}</header>
  {tab === 'Dashboard' && <><section className="stats"><article><span>Total Books</span><strong>{books.length}</strong><small>Catalog inventory</small></article><article><span>Available</span><strong>{available}</strong><small>Ready to issue</small></article><article><span>Issued</span><strong>{issued}</strong><small>Active checkouts</small></article><article><span>Members</span><strong>{members.length}</strong><small>{loans} active loan{loans === 1 ? '' : 's'}</small></article></section><BookPanel/></>}
  {tab === 'Books' && <BookPanel/>}
  {tab === 'Members' && <section className="panel"><div className="panel-head"><div><h2>Members</h2><p>Registered users and their active loans.</p></div></div><div className="member-grid">{members.map(m => <article className="member" key={m.id}><div className="avatar">{m.name.split(' ').map(x => x[0]).join('').slice(0,2)}</div><div><h3>{m.name}</h3><p>{m.email}</p><span>{m.id} · {m.activeLoans} active loan{m.activeLoans === 1 ? '' : 's'}</span></div></article>)}</div></section>}
  {showBook && <Modal title="Add a book" onClose={() => setShowBook(false)}><form className="form" onSubmit={addBook}><input autoFocus placeholder="Book title" value={bookForm.title} onChange={e => setBookForm({...bookForm,title:e.target.value})}/><input placeholder="Author" value={bookForm.author} onChange={e => setBookForm({...bookForm,author:e.target.value})}/><input placeholder="ISBN" value={bookForm.isbn} onChange={e => setBookForm({...bookForm,isbn:e.target.value})}/><select value={bookForm.category} onChange={e => setBookForm({...bookForm,category:e.target.value})}><option>General</option><option>Programming</option><option>Computer Science</option><option>Architecture</option><option>Fiction</option><option>Business</option></select><button className="primary">Add Book</button></form></Modal>}
  {showMember && <Modal title="Add a member" onClose={() => setShowMember(false)}><form className="form" onSubmit={addMember}><input autoFocus placeholder="Full name" value={memberForm.name} onChange={e => setMemberForm({...memberForm,name:e.target.value})}/><input type="email" placeholder="Email address" value={memberForm.email} onChange={e => setMemberForm({...memberForm,email:e.target.value})}/><button className="primary">Add Member</button></form></Modal>}{toast && <div className="toast" role="status">✓ {toast}</div>}</main></div>;
  function BookPanel() { return <section className="panel"><div className="panel-head"><div><h2>Book Inventory</h2><p>{filteredBooks.length} of {books.length} books shown.</p></div><div className="controls"><input aria-label="Search books" placeholder="Search title, author, ISBN..." value={query} onChange={e => setQuery(e.target.value)}/><select aria-label="Filter status" value={statusFilter} onChange={e => setStatusFilter(e.target.value)}><option>All</option><option>Available</option><option>Issued</option></select></div></div><div className="table-wrap"><table><thead><tr><th>Book</th><th>ISBN</th><th>Category</th><th>Status</th><th>Borrower</th><th>Action</th></tr></thead><tbody>{filteredBooks.map(b => { const borrower = members.find(m => m.id === b.issuedTo); return <tr key={b.id}><td><b>{b.title}</b><small>{b.author}</small></td><td>{b.isbn}</td><td>{b.category}</td><td><span className={`badge ${b.status.toLowerCase()}`}>{b.status}</span></td><td>{borrower?.name || '—'}</td><td><button className="action" onClick={() => b.status === 'Available' ? issueBook(b.id) : returnBook(b.id)}>{b.status === 'Available' ? 'Issue' : 'Return'}</button></td></tr>})}{filteredBooks.length === 0 && <tr><td colSpan="6" className="empty">No books match your filters.</td></tr>}</tbody></table><div className="issue-bar"><label>Issue selected book to:</label><select value={selectedMember} onChange={e => setSelectedMember(e.target.value)}><option value="">Select member...</option>{members.map(m => <option key={m.id} value={m.id}>{m.name} ({m.activeLoans} loans)</option>)}</select><span>Select a member, then click Issue.</span></div></div></section>; }
}
function Modal({title,onClose,children}) { return <div className="modal-backdrop" onMouseDown={onClose}><div className="modal" onMouseDown={e => e.stopPropagation()}><div className="modal-head"><h2>{title}</h2><button className="close" onClick={onClose}>×</button></div>{children}</div></div>; }
createRoot(document.getElementById('root')).render(<App />);
