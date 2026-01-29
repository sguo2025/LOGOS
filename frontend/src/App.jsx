import { Routes, Route } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import HomePage from './pages/HomePage'
import NL2SpELWorkbench from './pages/NL2SpELWorkbench'
import JavaModelingWorkbench from './pages/JavaModelingWorkbench'
import OntologyBrowser from './pages/OntologyBrowser'

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<HomePage />} />
        <Route path="nl2spel" element={<NL2SpELWorkbench />} />
        <Route path="modeling" element={<JavaModelingWorkbench />} />
        <Route path="ontology" element={<OntologyBrowser />} />
      </Route>
    </Routes>
  )
}

export default App
