
import './App.css'
import {useState, useEffect} from 'react'
import { useAuth } from './context/AuthContext'
import Login from './components/Login'
import { Register } from './components/Register'

interface FileMetadata {
    id: number
    fileName: string
    fileType: string
    size: number
    folder: string
    uploadedAt: string
    storagePath: string
}

function App() {
    const [folders, setFolders] = useState<string[]>([])
    const [selectedFolder, setSelectedFolder] = useState<string | null>(null)
    const [files, setFiles] = useState<FileMetadata[]>([])
    const [newFolderName, setNewFolderName] = useState('')
    const [loadingA, setLoadingA] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const[searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState<FileMetadata[]>([])
    const [searchMessage, setSearchMessage] = useState<string | null>('');
    const {user, loading, logout} = useAuth();


    const API_BASE = import.meta.env.VITE_BASE_URL;

    const searchFiles = async () =>
    {
        setSearchMessage(null)
        if(!searchQuery.trim() ){
            setSearchResults([])
            setSearchMessage('Search files'
            )
            return
        }
        try{
            setLoadingA(true)
            const response = await fetch(`${API_BASE}/search?query=${searchQuery}`)
            if(!response.ok) throw new Error ('Search failed here at response')
            const data:FileMetadata[] = await response.json()
            setSearchResults(data)
            setSelectedFolder(null)

            if(data.length === 0) {
                setSearchMessage(`No results found for "${searchQuery}"`)
            }

        } catch (e) {
            setError(e instanceof Error ? e.message : 'Search failed here')
        } finally {
            setLoadingA(false)
        }
    }

    // Fetch all files to extract unique folders
    const fetchFolders = async () => {
        try {
            setLoadingA(true)
            //all files
            const response = await fetch(API_BASE)
            if (!response.ok) throw new Error('Failed to fetch files')
            const allFiles: FileMetadata[] = await response.json()
            const uniqueFolders = [...new Set(allFiles.map(file => file.folder))]
            setFolders(uniqueFolders)

        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error')
        } finally {
            setLoadingA(false)
        }
    }

    // Fetch files in a specific folder
    const fetchFilesInFolder = async (folder: string) => {
        setSearchResults([]);
        setSearchQuery('');
        setSearchMessage(null);
        try {
            setLoadingA(true)
            const response = await fetch(`${API_BASE}/folder/${folder}`)
            if (!response.ok) throw new Error('Failed to fetch files')
            const folderFiles: FileMetadata[] = await response.json()
            setFiles(folderFiles)
            setSelectedFolder(folder)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error')
        } finally {
            setLoadingA(false)
        }
    }


    // Delete a folder
    const deleteFolder = async (folderName: string) => {
        if (!confirm(`Are you sure you want to delete the folder "${folderName}" and all its contents?`)) return

        try {
            setLoadingA(true)
            const response = await fetch(`${API_BASE}/folder/${folderName}`, {
                method: 'DELETE'
            })
            if (!response.ok) throw new Error('Failed to delete folder')
            setFolders(folders.filter(f => f !== folderName))
            if (selectedFolder === folderName) {
                setSelectedFolder(null)
                setFiles([])
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error')
        } finally {
            setLoadingA(false)
        }
    }

    // Delete a file
    const deleteFile = async (fileId: number) => {
        try {
            setLoadingA(true)
            const response = await fetch(`${API_BASE}/${fileId}`, {
                method: 'DELETE'
            })
            if (!response.ok) throw new Error('Failed to delete file')
            // Refresh files in current folder
            if (selectedFolder) {
                fetchFilesInFolder(selectedFolder)
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error')
        } finally {
            setLoadingA(false)
        }
    }

    const downloadFile = async  (fileId: number, fileName: string) => {
        try{
            const res = await fetch(`${API_BASE}/${fileId}/download`)
            if (!res.ok) throw new Error('Failed to download file')
            const blob = await res.blob()
            const url = window.URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = url
            a.download = fileName || 'download'
            document.body.appendChild(a)
            a.click()
            a.remove()
            window.URL.revokeObjectURL(url)
        }catch (e) {
            setError(e instanceof Error ? e.message : 'Unknown error in downloadFile')
        }
    }

    const generateReport = async() => {
        try {
            const res = await fetch(`${API_BASE}/report`)
            if(!res.ok) throw new Error('Failed to generate report')

            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href=url
            a.download = 'file-report.csv'
            document.body.appendChild(a)
            a.click()
            a.remove()
            window.URL.revokeObjectURL(url)
        } catch (e) {
            setError(e instanceof Error ? e.message : 'Unknown error in generateReport'
                )
        }
    }

    //upload file to a folder
    const uploadToFolder = async(folderName:string, file: File) => {
            if(!file) return
            const formData = new FormData()
            formData.append('file', file)
            formData.append('folder', folderName)

        try{
            setLoadingA(true)
            const response = await fetch(`${API_BASE}/upload`, {
                method: 'POST',
                body: formData
            })
            if(!response.ok) throw new Error('Failed to upload file on try')
            await fetchFolders()
            if(selectedFolder === folderName){
                await fetchFilesInFolder(folderName)
            }

        }catch (e) {
            setError(e instanceof Error ? e.message : 'Failed to upload file')
        } finally {
            setLoadingA(false)
        }
    }

    const uploadToNamedFolder = async (file: File) => {
        if (!file || !newFolderName.trim()) return

        // upload to the SAME folder name
        await uploadToFolder(newFolderName, file)

        //keep folder name so user can upload again
        const fileInput = document.getElementById('upload-new-folder') as HTMLInputElement
        if (fileInput) fileInput.value = ''
    }




    useEffect(() => {
        fetchFolders()
    }, [])

    if(loading) return <div>Loading...</div>;

    return (
        <>
        {user ? (


        <div className="app">
            <div className="app-container">
            <h1>File Management System</h1>

            {error && (
                <div className="error">
                    Error: {error}
                    <button onClick={() => setError(null)}>×</button>
                </div>
            )}

            <div className="folder-management">
                <p>
                    Files uploaded here will go to the same folder until you change the name.
                </p>
                <div>
                    {user?.photoURL && (
                        <img
                            src={user.photoURL}
                        />
                    )}
                    <p>User:<br />
                    {user?.displayName}
                    </p>
                </div>

                <div>
                    <input
                        type="text"
                        placeholder="Search Files.."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && searchFiles()}
                    />
                    <button
                        onClick={searchFiles} disabled={loadingA}
                    >Search</button>
                    {searchMessage && (
                        <p className="search-message">{searchMessage}</p>
                    )}
                </div>

                {searchResults.length > 0 && (
                    <div>
                        <h2>Search Results ({searchResults.length})</h2>
                        <div>
                            {searchResults.map(file => (
                                <div key={file.id}>
                                    <h4>{file.fileName}</h4>
                                    <p>Folder: {file.folder}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                <div className="create-folder-form">
                    <input
                        type="text"
                        placeholder="Enter folder name"
                        value={newFolderName}
                        onChange={(e) => setNewFolderName(e.target.value)}
                    />

                    <input
                        type="file"
                        id="upload-new-folder"
                        onChange={(e) => {
                            const f = e.target.files?.[0]
                            if (f) uploadToNamedFolder(f)
                        }}
                    />

                    <button
                        onClick={() => document.getElementById('upload-new-folder')?.click()}
                        disabled={loadingA || !newFolderName.trim()}
                    >
                        Upload File
                    </button>
                </div>
                <button onClick={()=> generateReport()}>Print report here</button>



                <div className="folders-list">
                    <h2>Folders ({folders.length})</h2>
                    {loadingA && <p>Loading...</p>}

                    {/*if no folders yet, then show this*/}
                    {!loadingA && folders.length === 0 && (
                        <p className="empty-state">No folders yet. Create your first folder above!</p>
                    )}

                    <div className="folders-grid">
                        {folders.
                        map(folder => (
                            <div key={folder} className={`folder-card ${selectedFolder === folder ? 'selected' : ''}`}>
                                <div className="folder-header">
                                    <h3 onClick={() => fetchFilesInFolder(folder)} style={{cursor: 'pointer'}}>
                                        📁 {folder}
                                    </h3>
                                    {/*upload file here*/}
                                    <input
                                        type="file"
                                        id={`upload-${folder}`}
                                        onChange={(e) => {
                                            const f = e.target.files?.[0] || null
                                            if (f) uploadToFolder(folder, f)
                                        }}
                                    />
                                    <button
                                        onClick={() => document.getElementById(`upload-${folder}`)?.click()}
                                        disabled={loadingA}
                                    >
                                        Upload
                                    </button>
                                    <button
                                        className="delete-btn"
                                        onClick={() => deleteFolder(folder)}
                                        disabled={loadingA}
                                    >
                                        🗑️
                                    </button>
                                </div>
                                <div className="folder-stats">
                                    <small>Click to view files</small>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {!searchResults.length && selectedFolder && (
                    <div className="files-list">
                        <h2>Files in "{selectedFolder}" ({files.length})</h2>
                        <div className="files-grid">
                            {files.map(file => (
                                <div key={file.id} className="file-card">
                                    <div className="file-header">
                                        <h4>{file.fileName}</h4>
                                        <button
                                            className="download-btn"
                                            onClick={()=> downloadFile(file.id, file.fileName)}
                                        >
                                            DOWNLOAD
                                        </button>
                                        <button
                                            className="delete-btn"
                                            onClick={() => deleteFile(file.id)}
                                            disabled={loadingA}
                                        >
                                            🗑️
                                        </button>
                                    </div>
                                    <div className="file-info">
                                        <p>Type: {file.fileType}</p>
                                        <p>Size: {(file.size / 1024).toFixed(2)} KB</p>
                                        <p>Uploaded: {new Date(file.uploadedAt).toLocaleDateString()}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
                </div>

            </div>

        </div>
        ) : (
            <>
            <Login />
            <Register />
            </>
        )
    }

    </>
)}

export default App
